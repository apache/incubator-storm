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
package org.apache.storm.redis.bolt;

import org.apache.storm.redis.common.config.JedisConfig;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.redis.common.config.JedisClusterConfig;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.container.JedisCommandsContainerBuilder;
import org.apache.storm.redis.common.container.JedisCommandsInstanceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

import java.util.Map;

/**
 * AbstractRedisBolt class is for users to implement custom bolts which makes interaction with Redis.
 * <p/>
 * Due to environment abstraction, AbstractRedisBolt provides JedisCommands which contains only single key operations.
 * <p/>
 * Custom Bolts may want to follow this pattern:
 * <p><blockquote><pre>
 * JedisCommands jedisCommands = null;
 * try {
 *     jedisCommand = getInstance();
 *     // do some works
 * } finally {
 *     if (jedisCommand != null) {
 *         returnInstance(jedisCommand);
 *     }
 * }
 * </pre></blockquote>
 *
 */
// TODO: Separate Jedis / JedisCluster to provide full operations for each environment to users
public abstract class AbstractRedisBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRedisBolt.class);

    protected OutputCollector collector;

    private transient JedisCommandsInstanceContainer container;

    private JedisConfig jedisConfig;

    /**
     * Constructor for single Redis or Redis cluster environment
     * @param config configuration for initializing JedisPool or JedisCluster
     */
    public AbstractRedisBolt(JedisConfig config) {
        this.jedisConfig = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        // FIXME: stores map (stormConf), topologyContext and expose these to derived classes
        this.collector = collector;

        if (jedisConfig instanceof JedisPoolConfig) {
            LOG.info("Using Jedis Pool Config");
            this.container = JedisCommandsContainerBuilder.build((JedisPoolConfig) jedisConfig);
        } else if (jedisConfig instanceof JedisClusterConfig) {
            LOG.info("Using Jedis Cluster Config");
            this.container = JedisCommandsContainerBuilder.build((JedisClusterConfig) jedisConfig);
        } else {
            throw new IllegalArgumentException("Jedis configuration not found");
        }
    }

    /**
     * Borrow JedisCommands instance from container.<p/>
     * JedisCommands is an interface which contains single key operations.
     * @return implementation of JedisCommands
     * @see JedisCommandsInstanceContainer#getInstance()
     */
    protected JedisCommands getInstance() {
        return this.container.getInstance();
    }

    /**
     * Return borrowed instance to container.
     * @param instance borrowed object
     */
    protected void returnInstance(JedisCommands instance) {
        this.container.returnInstance(instance);
    }
}
