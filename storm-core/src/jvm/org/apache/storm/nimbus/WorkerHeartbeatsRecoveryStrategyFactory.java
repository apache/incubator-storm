/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.nimbus;

import com.google.common.base.Preconditions;
import org.apache.storm.Config;
import org.apache.storm.utils.Utils;

import java.util.Map;

/**
 * Factory class for recovery strategy.
 */
public class WorkerHeartbeatsRecoveryStrategyFactory {

    public static IWorkerHeartbeatsRecoveryStrategy getStrategy(Map conf) {
        if (conf.get(Config.NIMBUS_WORKER_HEARTBEATS_RECOVERY_STRATEGY_CLASS) != null) {
            Object targetObj = Utils.newInstance((String) conf.get(Config.NIMBUS_WORKER_HEARTBEATS_RECOVERY_STRATEGY_CLASS));
            Preconditions.checkState(targetObj instanceof IWorkerHeartbeatsRecoveryStrategy, "{} must implements IWorkerHeartbeatsRecoveryStrategy", Config.NIMBUS_WORKER_HEARTBEATS_RECOVERY_STRATEGY_CLASS);
            ((IWorkerHeartbeatsRecoveryStrategy) targetObj).prepare(conf);
            return (IWorkerHeartbeatsRecoveryStrategy) targetObj;
        }

        IWorkerHeartbeatsRecoveryStrategy strategy = new TimeOutWorkerHeartbeatsRecoveryStrategy();
        strategy.prepare(conf);
        return strategy;
    }

}
