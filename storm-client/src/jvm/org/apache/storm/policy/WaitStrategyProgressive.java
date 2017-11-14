/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package org.apache.storm.policy;

import org.apache.storm.Config;
import org.apache.storm.utils.ObjectReader;

import java.util.Map;
import java.util.concurrent.locks.LockSupport;

/**
 * A Simple Progressive Wait Strategy
 * <p> Has three levels of idling. Stays in each level for a configured number of iterations before entering the next level.
 * Level 1 - No idling. Returns immediately. Stays in this level for `step` number of iterations.
 * Level 2 - Calls LockSupport.parkNanos(1). Stays in this level for `step X multiplier` iterations
 * Level 3 - Calls Thread.sleep(). Stays in this level indefinitely.
 *
 * <p>
 * The initial spin can be useful to prevent downstream bolt from repeatedly sleeping/parking when
 * the upstream component is a bit relatively slower. Allows downstream bolt can enter deeper wait states only
 * if the traffic to it appears to have reduced.
 * <p>
 * Provides control over how quickly to progress to the deeper wait level.
 * Larger the 'step' and 'multiplier', slower the progression.
 * Latency spike increases with every progression into the deeper wait level.
 */
public class WaitStrategyProgressive implements IWaitStrategy {
    private long sleepMillis;
    private int step;
    private int multiplier;

    @Override
    public void prepare(Map<String, Object> conf, WAIT_SITUATION waitSituation) {
        if (waitSituation == WAIT_SITUATION.CONSUME_WAIT) {
            sleepMillis = ObjectReader.getLong(conf.get(Config.TOPOLOGY_BOLT_WAIT_PROGRESSIVE_MILLIS));
            step = ObjectReader.getInt(conf.get(Config.TOPOLOGY_BOLT_WAIT_PROGRESSIVE_STEP));
            multiplier = ObjectReader.getInt(conf.get(Config.TOPOLOGY_BOLT_WAIT_PROGRESSIVE_MULTIPLIER));
        } else if (waitSituation == WAIT_SITUATION.BACK_PRESSURE_WAIT) {
            sleepMillis = ObjectReader.getLong(conf.get(Config.TOPOLOGY_BACKPRESSURE_WAIT_PROGRESSIVE_MILLIS));
            step = ObjectReader.getInt(conf.get(Config.TOPOLOGY_BACKPRESSURE_WAIT_PROGRESSIVE_STEP));
            multiplier = ObjectReader.getInt(conf.get(Config.TOPOLOGY_BACKPRESSURE_WAIT_PROGRESSIVE_MULTIPLIER));
        } else {
            throw new IllegalArgumentException("Unknown wait situation : " + waitSituation);
        }
    }

    @Override
    public int idle(int idleCounter) throws InterruptedException {
        if (idleCounter < step) {                     // level 1 - no waiting
            ++idleCounter;
        } else if (idleCounter < step * multiplier) { // level 2 - parkNanos(1L)
            ++idleCounter;
            LockSupport.parkNanos(1L);
        } else {                                      // level 3 - longer idling with Thread.sleep()
            Thread.sleep(sleepMillis);
        }
        return idleCounter;
    }
}
