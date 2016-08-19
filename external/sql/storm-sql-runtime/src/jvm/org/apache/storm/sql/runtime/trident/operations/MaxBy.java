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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.storm.sql.runtime.trident.operations;

import clojure.lang.Numbers;
import org.apache.storm.sql.runtime.trident.TridentUtils;
import org.apache.storm.trident.operation.CombinerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;

public class MaxBy implements CombinerAggregator<Number> {

    private final String inputFieldName;

    public MaxBy(String inputFieldName) {
        this.inputFieldName = inputFieldName;
    }

    @Override
    public Number init(TridentTuple tuple) {
        if (tuple.isEmpty()) {
            return zero();
        }

        return TridentUtils.valueFromTuple(tuple, inputFieldName);
    }

    @Override
    public Number combine(Number val1, Number val2) {
        return (Number) Numbers.max(val1, val2);
    }

    @Override
    public Number zero() {
        return Double.MIN_VALUE;
    }
}
