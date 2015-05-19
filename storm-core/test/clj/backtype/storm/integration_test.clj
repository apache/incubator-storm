;; Licensed to the Apache Software Foundation (ASF) under one
;; or more contributor license agreements.  See the NOTICE file
;; distributed with this work for additional information
;; regarding copyright ownership.  The ASF licenses this file
;; to you under the Apache License, Version 2.0 (the
;; "License"); you may not use this file except in compliance
;; with the License.  You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.
(ns backtype.storm.integration-test
  (:require [backtype.storm.config :as c]
            [backtype.storm.thrift :as thrift]
            [backtype.storm.daemon.common :as daemon]
            [backtype.storm.util :as util]
            [backtype.storm.testing :as testing]
            [backtype.storm.clojure :refer :all]
            [clojure.test :refer :all])
  (:import [backtype.storm.topology TopologyBuilder]
           [backtype.storm.generated InvalidTopologyException SubmitOptions TopologyInitialStatus]
           [backtype.storm.testing TestWordCounter TestWordSpout TestGlobalCount
                                   TestAggregatesCounter TestConfBolt AckFailMapTracker AckTracker TestPlannerSpout]
           [backtype.storm.tuple Fields]))

(deftest test-basic-topology
  (doseq [zmq-on? [true false]]
    (testing/with-simulated-time-local-cluster [cluster :supervisors 4
                                        :daemon-conf {c/STORM-LOCAL-MODE-ZMQ zmq-on?}]
      (let [topology (thrift/mk-topology
                      {"1" (thrift/mk-spout-spec (TestWordSpout. true) :parallelism-hint 3)}
                      {"2" (thrift/mk-bolt-spec {"1" ["word"]} (TestWordCounter.) :parallelism-hint 4)
                       "3" (thrift/mk-bolt-spec {"1" :global} (TestGlobalCount.))
                       "4" (thrift/mk-bolt-spec {"2" :global} (TestAggregatesCounter.))
                       })
            results (testing/complete-topology cluster
                                       topology
                                       :mock-sources {"1" [["nathan"] ["bob"] ["joey"] ["nathan"]]}
                                       :storm-conf {c/TOPOLOGY-WORKERS 2})]
        (is (testing/ms= [["nathan"] ["bob"] ["joey"] ["nathan"]]
                 (testing/read-tuples results "1")))
        (is (testing/ms= [["nathan" 1] ["nathan" 2] ["bob" 1] ["joey" 1]]
                 (testing/read-tuples results "2")))
        (is (= [[1] [2] [3] [4]]
               (testing/read-tuples results "3")))
        (is (= [[1] [2] [3] [4]]
               (testing/read-tuples results "4")))
        ))))

(defbolt emit-task-id ["tid"] {:prepare true}
  [conf context collector]
  (let [tid (.getThisTaskIndex context)]
    (bolt
      (execute [tuple]
        (emit-bolt! collector [tid] :anchor tuple)
        (ack! collector tuple)
        ))))

(deftest test-multi-tasks-per-executor
  (testing/with-simulated-time-local-cluster [cluster :supervisors 4]
    (let [topology (thrift/mk-topology
                    {"1" (thrift/mk-spout-spec (TestWordSpout. true))}
                    {"2" (thrift/mk-bolt-spec {"1" :shuffle} emit-task-id
                      :parallelism-hint 3
                      :conf {c/TOPOLOGY-TASKS 6})
                     })
          results (testing/complete-topology cluster
                                     topology
                                     :mock-sources {"1" [["a"] ["a"] ["a"] ["a"] ["a"] ["a"]]})]
      (is (testing/ms= [[0] [1] [2] [3] [4] [5]]
               (testing/read-tuples results "2")))
      )))

(defbolt ack-every-other {} {:prepare true}
  [conf context collector]
  (let [state (atom -1)]
    (bolt
      (execute [tuple]
        (let [val (swap! state -)]
          (when (pos? val)
            (ack! collector tuple)
            ))))))

(defn assert-loop [afn ids]
  (while (not (every? afn ids))
    (Thread/sleep 1)))

(defn assert-acked [tracker & ids]
  (assert-loop #(.isAcked tracker %) ids))

(defn assert-failed [tracker & ids]
  (assert-loop #(.isFailed tracker %) ids))

(deftest test-timeout
  (testing/with-simulated-time-local-cluster [cluster :daemon-conf {c/TOPOLOGY-ENABLE-MESSAGE-TIMEOUTS true}]
    (let [feeder (testing/feeder-spout ["field1"])
          tracker (AckFailMapTracker.)
          _ (.setAckFailDelegate feeder tracker)
          topology (thrift/mk-topology
                     {"1" (thrift/mk-spout-spec feeder)}
                     {"2" (thrift/mk-bolt-spec {"1" :global} ack-every-other)})]
      (testing/submit-local-topology (:nimbus cluster)
                             "timeout-tester"
                             {c/TOPOLOGY-MESSAGE-TIMEOUT-SECS 10}
                             topology)
      (.feed feeder ["a"] 1)
      (.feed feeder ["b"] 2)
      (.feed feeder ["c"] 3)
      (testing/advance-cluster-time cluster 9)
      (assert-acked tracker 1 3)
      (is (not (.isFailed tracker 2)))
      (testing/advance-cluster-time cluster 12)
      (assert-failed tracker 2)
      )))

(defn mk-validate-topology-1 []
  (thrift/mk-topology
                    {"1" (thrift/mk-spout-spec (TestWordSpout. true) :parallelism-hint 3)}
                    {"2" (thrift/mk-bolt-spec {"1" ["word"]} (TestWordCounter.) :parallelism-hint 4)}))

(defn mk-invalidate-topology-1 []
  (thrift/mk-topology
                    {"1" (thrift/mk-spout-spec (TestWordSpout. true) :parallelism-hint 3)}
                    {"2" (thrift/mk-bolt-spec {"3" ["word"]} (TestWordCounter.) :parallelism-hint 4)}))

(defn mk-invalidate-topology-2 []
  (thrift/mk-topology
                    {"1" (thrift/mk-spout-spec (TestWordSpout. true) :parallelism-hint 3)}
                    {"2" (thrift/mk-bolt-spec {"1" ["non-exists-field"]} (TestWordCounter.) :parallelism-hint 4)}))

(defn mk-invalidate-topology-3 []
  (thrift/mk-topology
                    {"1" (thrift/mk-spout-spec (TestWordSpout. true) :parallelism-hint 3)}
                    {"2" (thrift/mk-bolt-spec {["1" "non-exists-stream"] ["word"]} (TestWordCounter.) :parallelism-hint 4)}))

(defn try-complete-wc-topology [cluster topology]
  (try (do
         (testing/complete-topology cluster
                            topology
                            :mock-sources {"1" [["nathan"] ["bob"] ["joey"] ["nathan"]]}
                            :storm-conf {c/TOPOLOGY-WORKERS 2})
         false)
       (catch InvalidTopologyException e true)))

(deftest test-validate-topology-structure
  (testing/with-simulated-time-local-cluster [cluster :supervisors 4]
    (let [any-error1? (try-complete-wc-topology cluster (mk-validate-topology-1))
          any-error2? (try-complete-wc-topology cluster (mk-invalidate-topology-1))
          any-error3? (try-complete-wc-topology cluster (mk-invalidate-topology-2))
          any-error4? (try-complete-wc-topology cluster (mk-invalidate-topology-3))]
      (is (= any-error1? false))
      (is (= any-error2? true))
      (is (= any-error3? true))
      (is (= any-error4? true)))))

(defbolt identity-bolt ["num"]
  [tuple collector]
  (emit-bolt! collector (.getValues tuple) :anchor tuple)
  (ack! collector tuple))

(deftest test-system-stream
  ;; this test works because mocking a spout splits up the tuples evenly among the tasks
  (testing/with-simulated-time-local-cluster [cluster]
      (let [topology (thrift/mk-topology
                      {"1" (thrift/mk-spout-spec (TestWordSpout. true) :p 3)}
                      {"2" (thrift/mk-bolt-spec {"1" ["word"] ["1" "__system"] :global} identity-bolt :p 1)
                       })
            results (testing/complete-topology cluster
                                       topology
                                       :mock-sources {"1" [["a"] ["b"] ["c"]]}
                                       :storm-conf {c/TOPOLOGY-WORKERS 2})]
        (is (testing/ms= [["a"] ["b"] ["c"] ["startup"] ["startup"] ["startup"]]
                 (testing/read-tuples results "2")))
        )))

(defn ack-tracking-feeder [fields]
  (let [tracker (AckTracker.)]
    [(doto (testing/feeder-spout fields)
       (.setAckFailDelegate tracker))
     (fn [val]
       (is (= (.getNumAcks tracker) val))
       (.resetNumAcks tracker)
       )]
    ))

(defbolt branching-bolt ["num"]
  {:params [amt]}
  [tuple collector]
  (doseq [i (range amt)]
    (emit-bolt! collector [i] :anchor tuple))
  (ack! collector tuple))

(defbolt agg-bolt ["num"] {:prepare true :params [amt]}
  [conf context collector]
  (let [seen (atom [])]
    (bolt
      (execute [tuple]
        (swap! seen conj tuple)
        (when (= (count @seen) amt)
          (emit-bolt! collector [1] :anchor @seen)
          (doseq [s @seen]
            (ack! collector s))
          (reset! seen [])
          )))
      ))

(defbolt ack-bolt {}
  [tuple collector]
  (ack! collector tuple))

(deftest test-acking
  (testing/with-tracked-cluster [cluster]
    (let [[feeder1 checker1] (ack-tracking-feeder ["num"])
          [feeder2 checker2] (ack-tracking-feeder ["num"])
          [feeder3 checker3] (ack-tracking-feeder ["num"])
          tracked (testing/mk-tracked-topology
                   cluster
                   (topology
                     {"1" (spout-spec feeder1)
                      "2" (spout-spec feeder2)
                      "3" (spout-spec feeder3)}
                     {"4" (bolt-spec {"1" :shuffle} (branching-bolt 2))
                      "5" (bolt-spec {"2" :shuffle} (branching-bolt 4))
                      "6" (bolt-spec {"3" :shuffle} (branching-bolt 1))
                      "7" (bolt-spec
                            {"4" :shuffle
                            "5" :shuffle
                            "6" :shuffle}
                            (agg-bolt 3))
                      "8" (bolt-spec {"7" :shuffle} (branching-bolt 2))
                      "9" (bolt-spec {"8" :shuffle} ack-bolt)}
                     ))]
      (testing/submit-local-topology (:nimbus cluster)
                             "acking-test1"
                             {}
                             (:topology tracked))
      (.feed feeder1 [1])
      (testing/tracked-wait tracked 1)
      (checker1 0)
      (.feed feeder2 [1])
      (testing/tracked-wait tracked 1)
      (checker1 1)
      (checker2 1)
      (.feed feeder1 [1])
      (testing/tracked-wait tracked 1)
      (checker1 0)
      (.feed feeder1 [1])
      (testing/tracked-wait tracked 1)
      (checker1 1)
      (.feed feeder3 [1])
      (testing/tracked-wait tracked 1)
      (checker1 0)
      (checker3 0)
      (.feed feeder2 [1])
      (testing/tracked-wait tracked 1)
      (checker1 1)
      (checker2 1)
      (checker3 1)

      )))

(deftest test-ack-branching
  (testing/with-tracked-cluster [cluster]
    (let [[feeder checker] (ack-tracking-feeder ["num"])
          tracked (testing/mk-tracked-topology
                   cluster
                   (topology
                     {"1" (spout-spec feeder)}
                     {"2" (bolt-spec {"1" :shuffle} identity-bolt)
                      "3" (bolt-spec {"1" :shuffle} identity-bolt)
                      "4" (bolt-spec
                            {"2" :shuffle
                             "3" :shuffle}
                             (agg-bolt 4))}))]
      (testing/submit-local-topology (:nimbus cluster)
                             "test-acking2"
                             {}
                             (:topology tracked))
      (.feed feeder [1])
      (testing/tracked-wait tracked 1)
      (checker 0)
      (.feed feeder [1])
      (testing/tracked-wait tracked 1)
      (checker 2)
      )))

(defbolt dup-anchor ["num"]
  [tuple collector]
  (emit-bolt! collector [1] :anchor [tuple tuple])
  (ack! collector tuple))

(def bolt-prepared? (atom false))
(defbolt prepare-tracked-bolt [] {:prepare true}
  [conf context collector]
  (reset! bolt-prepared? true)
  (bolt
   (execute [tuple]
            (ack! collector tuple))))

(def spout-opened? (atom false))
(defspout open-tracked-spout ["val"]
  [conf context collector]
  (reset! spout-opened? true)
  (spout
   (nextTuple [])))

(deftest test-submit-inactive-topology
  (testing/with-simulated-time-local-cluster [cluster :daemon-conf {c/TOPOLOGY-ENABLE-MESSAGE-TIMEOUTS true}]
    (let [feeder (testing/feeder-spout ["field1"])
          tracker (AckFailMapTracker.)
          _ (.setAckFailDelegate feeder tracker)
          topology (thrift/mk-topology
                    {"1" (thrift/mk-spout-spec feeder)
                     "2" (thrift/mk-spout-spec open-tracked-spout)}
                    {"3" (thrift/mk-bolt-spec {"1" :global} prepare-tracked-bolt)})]
      (reset! bolt-prepared? false)
      (reset! spout-opened? false)

      (testing/submit-local-topology-with-opts (:nimbus cluster)
        "test"
        {c/TOPOLOGY-MESSAGE-TIMEOUT-SECS 10}
        topology
        (SubmitOptions. TopologyInitialStatus/INACTIVE))
      (.feed feeder ["a"] 1)
      (testing/advance-cluster-time cluster 9)
      (is (not @bolt-prepared?))
      (is (not @spout-opened?))
      (.activate (:nimbus cluster) "test")

      (testing/advance-cluster-time cluster 12)
      (assert-acked tracker 1)
      (is @bolt-prepared?)
      (is @spout-opened?))))

(deftest test-acking-self-anchor
  (testing/with-tracked-cluster [cluster]
    (let [[feeder checker] (ack-tracking-feeder ["num"])
          tracked (testing/mk-tracked-topology
                   cluster
                   (topology
                     {"1" (spout-spec feeder)}
                     {"2" (bolt-spec {"1" :shuffle} dup-anchor)
                      "3" (bolt-spec {"2" :shuffle} ack-bolt)}))]
      (testing/submit-local-topology (:nimbus cluster)
                             "test"
                             {}
                             (:topology tracked))
      (.feed feeder [1])
      (testing/tracked-wait tracked 1)
      (checker 1)
      (.feed feeder [1])
      (.feed feeder [1])
      (.feed feeder [1])
      (testing/tracked-wait tracked 3)
      (checker 3)
      )))

;; (defspout ConstantSpout ["val"] {:prepare false}
;;   [collector]
;;   (Time/sleep 100)
;;   (emit-spout! collector [1]))

;; (def errored (atom false))
;; (def restarted (atom false))

;; (defbolt local-error-checker {} [tuple collector]
;;   (when-not @errored
;;     (reset! errored true)
;;     (println "erroring")
;;     (throw (RuntimeException.)))
;;   (when-not @restarted (println "restarted"))
;;   (reset! restarted true))

;; (deftest test-no-halt-local-mode
;;   (with-simulated-time-local-cluster [cluster]
;;       (let [topology (topology
;;                       {1 (spout-spec ConstantSpout)}
;;                       {2 (bolt-spec {1 :shuffle} local-error-checker)
;;                        })]
;;         (submit-local-topology (:nimbus cluster)
;;                                "test"
;;                                {}
;;                                topology)
;;         (while (not @restarted)
;;           (advance-time-ms! 100))
;;         )))

(defspout IncSpout ["word"]
  [conf context collector]
  (let [state (atom 0)]
    (spout
     (nextTuple []
       (Thread/sleep 100)
       (emit-spout! collector [@state] :id 1)
       )
     (ack [id]
       (swap! state inc))
     )))


(defspout IncSpout2 ["word"] {:params [prefix]}
  [conf context collector]
  (let [state (atom 0)]
    (spout
     (nextTuple []
       (Thread/sleep 100)
       (swap! state inc)
       (emit-spout! collector [(str prefix "-" @state)])
       )
     )))

;; (deftest test-clojure-spout
;;   (with-local-cluster [cluster]
;;     (let [nimbus (:nimbus cluster)
;;           top (topology
;;                {1 (spout-spec IncSpout)}
;;                {}
;;                )]
;;       (submit-local-topology nimbus
;;                              "spout-test"
;;                              {TOPOLOGY-DEBUG true
;;                               TOPOLOGY-MESSAGE-TIMEOUT-SECS 3}
;;                              top)
;;       (Thread/sleep 10000)
;;       (.killTopology nimbus "spout-test")
;;       (Thread/sleep 10000)
;;       )))

(deftest test-kryo-decorators-config
  (testing/with-simulated-time-local-cluster [cluster
                                      :daemon-conf {c/TOPOLOGY-SKIP-MISSING-KRYO-REGISTRATIONS true
                                                    c/TOPOLOGY-KRYO-DECORATORS ["this-is-overriden"]}]
    (util/letlocals
     (bind builder (TopologyBuilder.))
     (.setSpout builder "1" (TestPlannerSpout. (Fields. ["conf"])))
     (-> builder
         (.setBolt "2"
                   (TestConfBolt.
                    {c/TOPOLOGY-KRYO-DECORATORS ["one" "two"]}))
         (.shuffleGrouping "1"))

     (bind results
           (testing/complete-topology cluster
                              (.createTopology builder)
                              :storm-conf {c/TOPOLOGY-KRYO-DECORATORS ["one" "three"]}
                              :mock-sources {"1" [[c/TOPOLOGY-KRYO-DECORATORS]]}))
     (is (= {"topology.kryo.decorators" (list "one" "two" "three")}
            (->> (testing/read-tuples results "2")
                 (apply concat)
                 (apply hash-map)))))))

(deftest test-component-specific-config
  (testing/with-simulated-time-local-cluster [cluster
                                      :daemon-conf {c/TOPOLOGY-SKIP-MISSING-KRYO-REGISTRATIONS true}]
    (util/letlocals
     (bind builder (TopologyBuilder.))
     (.setSpout builder "1" (TestPlannerSpout. (Fields. ["conf"])))
     (-> builder
         (.setBolt "2"
                   (TestConfBolt.
                    {"fake.config" 123
                     c/TOPOLOGY-MAX-TASK-PARALLELISM 20
                     c/TOPOLOGY-MAX-SPOUT-PENDING 30
                     c/TOPOLOGY-KRYO-REGISTER [{"fake.type" "bad.serializer"}
                                             {"fake.type2" "a.serializer"}]
                     }))
         (.shuffleGrouping "1")
         (.setMaxTaskParallelism (int 2))
         (.addConfiguration "fake.config2" 987)
         )


     (bind results
           (testing/complete-topology cluster
                              (.createTopology builder)
                              :storm-conf {c/TOPOLOGY-KRYO-REGISTER [{"fake.type" "good.serializer" "fake.type3" "a.serializer3"}]}
                              :mock-sources {"1" [["fake.config"]
                                                  [c/TOPOLOGY-MAX-TASK-PARALLELISM]
                                                  [c/TOPOLOGY-MAX-SPOUT-PENDING]
                                                  ["fake.config2"]
                                                  [c/TOPOLOGY-KRYO-REGISTER]
                                                  ]}))
     (is (= {"fake.config" 123
             "fake.config2" 987
             c/TOPOLOGY-MAX-TASK-PARALLELISM 2
             c/TOPOLOGY-MAX-SPOUT-PENDING 30
             c/TOPOLOGY-KRYO-REGISTER {"fake.type" "good.serializer"
                                     "fake.type2" "a.serializer"
                                     "fake.type3" "a.serializer3"}}
            (->> (testing/read-tuples results "2")
                 (apply concat)
                 (apply hash-map))
            ))
     )))

(defbolt hooks-bolt ["emit" "ack" "fail" "executed"] {:prepare true}
  [conf context collector]
  (let [acked (atom 0)
        failed (atom 0)
        executed (atom 0)
        emitted (atom 0)]
    (.addTaskHook context
                  (reify backtype.storm.hooks.ITaskHook
                    (prepare [this conf context]
                      )
                    (cleanup [this]
                      )
                    (emit [this info]
                      (swap! emitted inc))
                    (boltAck [this info]
                      (swap! acked inc))
                    (boltFail [this info]
                      (swap! failed inc))
                    (boltExecute [this info]
                      (swap! executed inc))
                      ))
    (bolt
     (execute [tuple]
        (emit-bolt! collector [@emitted @acked @failed @executed])
        (if (= 0 (- @acked @failed))
          (ack! collector tuple)
          (fail! collector tuple))
        ))))

(deftest test-hooks
  (testing/with-simulated-time-local-cluster [cluster]
    (let [topology (topology {"1" (spout-spec (TestPlannerSpout. (Fields. ["conf"])))
                              }
                             {"2" (bolt-spec {"1" :shuffle}
                                             hooks-bolt)
                              })
          results (testing/complete-topology cluster
                                     topology
                                     :mock-sources {"1" [[1]
                                                         [1]
                                                         [1]
                                                         [1]
                                                         ]})]
      (is (= [[0 0 0 0]
              [2 1 0 1]
              [4 1 1 2]
              [6 2 1 3]]
             (testing/read-tuples results "2")
             )))))

(defbolt report-errors-bolt {}
  [tuple collector]
  (doseq [i (range (.getValue tuple 0))]
    (report-error! collector (RuntimeException.)))
  (ack! collector tuple))

(deftest test-throttled-errors
  (testing/with-simulated-time
    (testing/with-tracked-cluster [cluster]
      (let [state (:storm-cluster-state cluster)
            [feeder checker] (ack-tracking-feeder ["num"])
            tracked (testing/mk-tracked-topology
                     cluster
                     (topology
                       {"1" (spout-spec feeder)}
                       {"2" (bolt-spec {"1" :shuffle} report-errors-bolt)}))
            _       (testing/submit-local-topology (:nimbus cluster)
                                             "test-errors"
                                             {c/TOPOLOGY-ERROR-THROTTLE-INTERVAL-SECS 10
                                              c/TOPOLOGY-MAX-ERROR-REPORT-PER-INTERVAL 4
                                              c/TOPOLOGY-DEBUG true
                                              }
                                             (:topology tracked))
            storm-id (daemon/get-storm-id state "test-errors")
            errors-count (fn [] (count (.errors state storm-id "2")))]

        (is (nil? (.last-error state storm-id "2")))

        ;; so it launches the topology
        (testing/advance-cluster-time cluster 2)
        (.feed feeder [6])
        (testing/tracked-wait tracked 1)
        (is (= 4 (errors-count)))
        (is (.last-error state storm-id "2"))

        (testing/advance-time-secs! 5)
        (.feed feeder [2])
        (testing/tracked-wait tracked 1)
        (is (= 4 (errors-count)))
        (is (.last-error state storm-id "2"))

        (testing/advance-time-secs! 6)
        (.feed feeder [2])
        (testing/tracked-wait tracked 1)
        (is (= 6 (errors-count)))
        (is (.last-error state storm-id "2"))

        (testing/advance-time-secs! 6)
        (.feed feeder [3])
        (testing/tracked-wait tracked 1)
        (is (= 8 (errors-count)))
        (is (.last-error state storm-id "2"))))))


(deftest test-acking-branching-complex
  ;; test acking with branching in the topology
  )


(deftest test-fields-grouping
  ;; 1. put a shitload of random tuples through it and test that counts are right
  ;; 2. test that different spouts with different phints group the same way
  )

(deftest test-all-grouping
  )

(deftest test-direct-grouping
  )
