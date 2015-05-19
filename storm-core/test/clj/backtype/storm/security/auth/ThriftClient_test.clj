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
(ns backtype.storm.security.auth.ThriftClient-test
  (:require [backtype.storm.util :as util]
            [backtype.storm.config :as c]
            [clojure.test :refer :all])
  (:import [backtype.storm.security.auth ThriftClient ThriftConnectionType]
           [org.apache.thrift.transport TTransportException]))

(deftest test-ctor-throws-if-port-invalid
  (let [conf (merge
              (c/read-default-config)
              {c/STORM-NIMBUS-RETRY-TIMES 0})
        timeout (Integer. 30)]
    (is (util/thrown-cause? java.lang.IllegalArgumentException
      (ThriftClient. conf ThriftConnectionType/DRPC "bogushost" (int -1) timeout)))
    (is (util/thrown-cause? java.lang.IllegalArgumentException
        (ThriftClient. conf ThriftConnectionType/DRPC "bogushost" (int 0) timeout)))
  )
)

(deftest test-ctor-throws-if-host-not-set
  (let [conf (merge
              (c/read-default-config)
              {c/STORM-NIMBUS-RETRY-TIMES 0})
        timeout (Integer. 60)]
    (is (util/thrown-cause? TTransportException
         (ThriftClient. conf ThriftConnectionType/DRPC "" (int 4242) timeout)))
    (is (util/thrown-cause? IllegalArgumentException
        (ThriftClient. conf ThriftConnectionType/DRPC nil (int 4242) timeout)))
  )
)
