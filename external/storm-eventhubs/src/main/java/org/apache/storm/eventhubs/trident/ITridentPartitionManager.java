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
package org.apache.storm.eventhubs.trident;

import org.apache.storm.eventhubs.core.EventHubMessage;

import com.microsoft.azure.eventhubs.EventHubException;

import java.io.IOException;
import java.util.List;

public interface ITridentPartitionManager {
    void open(String offset) throws IOException, EventHubException;
    
    void close();
    
    String getPartitionId();
  
    /**
     * receive a batch of messages from EvenHub up to "count" messages
     * @param offset the starting offset
     * @param count max number of messages in this batch
     * @return list of EventData, if failed to receive, return empty list
     */
    List<EventHubMessage> receiveBatch(String offset, int count) throws IOException, EventHubException;
}
