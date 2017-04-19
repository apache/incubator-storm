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

import backtype.storm.task.OutputCollector;
import backtype.storm.topology.FailedException;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.kafka.trident.mapper.TridentTupleToKafkaMapper;
import storm.kafka.trident.selector.KafkaTopicSelector;
import storm.trident.operation.TridentCollector;
import storm.trident.state.State;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TridentKafkaState implements State {
    private static final Logger LOG = LoggerFactory.getLogger(TridentKafkaState.class);

    public static final String KAFKA_BROKER_PROPERTIES = "kafka.broker.properties";

    private Producer producer;
    private OutputCollector collector;

    private TridentTupleToKafkaMapper mapper;
    private KafkaTopicSelector topicSelector;

    public TridentKafkaState withTridentTupleToKafkaMapper(TridentTupleToKafkaMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    public TridentKafkaState withKafkaTopicSelector(KafkaTopicSelector selector) {
        this.topicSelector = selector;
        return this;
    }

    @Override
    public void beginCommit(Long txid) {
        LOG.debug("beginCommit is Noop.");
    }

    @Override
    public void commit(Long txid) {
        LOG.debug("commit is Noop.");
    }

    public void prepare(Map stormConf) {
        Validate.notNull(mapper, "mapper can not be null");
        Validate.notNull(topicSelector, "topicSelector can not be null");
        Map configMap = (Map) stormConf.get(KAFKA_BROKER_PROPERTIES);
        Properties properties = new Properties();
        properties.putAll(configMap);
        ProducerConfig config = new ProducerConfig(properties);
        producer = new Producer(config);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void updateState(List<TridentTuple> tuples, TridentCollector collector) {
        String topic = null;
        List<KeyedMessage> batchList = new ArrayList<KeyedMessage>(tuples.size());
        // Creating Batch
        for (TridentTuple tuple : tuples) {
            try {
                topic = topicSelector.getTopic(tuple);
                if (topic != null) {
                    batchList.add(new KeyedMessage(topic, mapper.getKeyFromTuple(tuple),
                            mapper.getMessageFromTuple(tuple)));
                } else {
                    LOG.warn("skipping key = " + mapper.getKeyFromTuple(tuple)
                            + ", topic selector returned null.");
                }
            } catch (Exception ex) {
                String errorMsg = "Error while filling up List for Batching";
                LOG.warn(errorMsg, ex);
                throw new FailedException(errorMsg, ex);
            }
        }
        // Sending Batch
        try {
            producer.send(batchList);
            LOG.debug("Sending the Batch " + batchList.size());
        } catch (Exception ex) {
            String errorMsg = "Could not send messages = " + tuples + " to topic = " + topic;
            LOG.warn(errorMsg, ex);
            throw new FailedException(errorMsg, ex);
        }
    }
}
