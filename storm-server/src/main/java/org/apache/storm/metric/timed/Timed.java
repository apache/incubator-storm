/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.apache.storm.metric.timed;

import com.codahale.metrics.Timer;

import java.util.concurrent.atomic.AtomicReference;

public class Timed<T> implements TimerDecorated {
    private final T measured;
    //TODO: Does this have to volatile?
    private final AtomicReference<Timer.Context> timingRef;

    public Timed(T measured, Timer timer) {
        this.measured = measured;
        timingRef = new AtomicReference<>(timer.time());
    }

    public T getMeasured() {
        return measured;
    }

    @Override
    public boolean hasStopped() {
        return hasStopped(timingRef);
    }

    @Override
    public long stopTiming() {
        return stopTiming(timingRef);
    }
}
