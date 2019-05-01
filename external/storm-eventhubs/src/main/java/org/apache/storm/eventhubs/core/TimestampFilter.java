/*******************************************************************************
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
 *******************************************************************************/
package org.apache.storm.eventhubs.core;

import java.time.Instant;

import com.microsoft.azure.eventhubs.EventPosition;

public class TimestampFilter implements IEventFilter {
    Instant time = null;
	  
    public TimestampFilter(Instant time) {
        this.time = time;
    }
    
    @Override
    public EventPosition getEventPosition() {
    	return EventPosition.fromEnqueuedTime(this.time);
    }

    @Override
    public String toString() {
        if (this.time != null) {
            return Long.toString(this.time.toEpochMilli());
        }
        return null;
    }
}
