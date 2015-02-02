/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package storm.kafka.trident;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaConfig;
import storm.kafka.KafkaFactory;


public class TridentKafkaConfig extends KafkaConfig {


    public IBatchCoordinator coordinator = new DefaultCoordinator();

    /**
     * @deprecated  use kafkaFactory based construction instead
     */
    @Deprecated
    public TridentKafkaConfig(BrokerHosts hosts, String topic) {
        super(hosts, topic);
    }

    /**
     * @deprecated  use kafkaFactory based construction instead
     */
    @Deprecated
    public TridentKafkaConfig(BrokerHosts hosts, String topic, String clientId) {
        super(hosts, topic, clientId);
    }

    public TridentKafkaConfig(String topic, String clientId, KafkaFactory kafkaFactory) {
        super(topic, clientId, kafkaFactory);
    }

    public TridentKafkaConfig(String topic, KafkaFactory kafkaFactory) {
        super(topic, kafkaFactory);
    }

}
