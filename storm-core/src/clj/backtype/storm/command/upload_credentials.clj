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
(ns backtype.storm.command.upload-credentials
  (:require [clojure.tools.cli :refer [cli]]
            [backtype.storm.util :as util]
            [backtype.storm.log :as log])
  (:import [backtype.storm StormSubmitter]
           [java.util Properties]
           [java.io FileReader])
  (:gen-class))

(defn read-map [file-name]
  (let [props (Properties. )
        _ (.load props (FileReader. file-name))]
    (util/clojurify-structure props)))

(defn -main [& args]
  (let [[{cred-file :file} [name & rawCreds]] (cli args ["-f" "--file" :default nil])
        _ (when (and rawCreds (not (even? (.size rawCreds)))) ;; TODO: is the (and rawCreds needed here?
            (throw (RuntimeException.  "Need an even number of arguments to make a map")))
        mapping (if rawCreds (apply assoc {} rawCreds) {})
        file-mapping (if (nil? cred-file) {} (read-map cred-file))]
      (StormSubmitter/pushCredentials name {} (merge file-mapping mapping))
      (log/log-message "Uploaded new creds to topology: " name)))
