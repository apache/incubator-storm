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
package org.apache.storm.eventhubs.spout;

import org.apache.storm.eventhubs.core.EventHubConfig;
import org.apache.storm.eventhubs.core.IEventHubReceiver;
import org.apache.storm.eventhubs.core.IEventHubReceiverFactory;
import org.apache.storm.eventhubs.core.MessageId;
import org.apache.storm.eventhubs.state.IStateStore;
import org.apache.storm.spout.SpoutOutputCollector;

/**
 * Mocks EventHubSpout's caller (storm framework)
 */
public class EventHubSpoutCallerMock {
	public static final String statePathPrefix = "/eventhubspout/TestTopo/namespace/entityname/partitions/";
	private EventHubSpout spout;
	private IStateStore stateStore;
	private SpoutOutputCollectorMock collector;
  
  public EventHubSpoutCallerMock(int totalPartitions,
      int totalTasks, int taskIndex, int checkpointInterval) {
	  this.stateStore = new StateStoreMock();
	  EventHubSpoutConfig conf = new EventHubSpoutConfig("username", "password",
			  "namespace", "entityname", totalPartitions, "zookeeper", checkpointInterval, 1024);
	  conf.setTopologyName("TestTopo");
    
	  IEventHubReceiverFactory recvFactory = new IEventHubReceiverFactory() {
		  private static final long serialVersionUID = 1L;

		  @Override
		  public IEventHubReceiver create(EventHubConfig config, String partitionId) {
			  return new EventHubReceiverMock(partitionId);
		  }
	  };
	  // mock state store and receiver
	  this.spout = new EventHubSpout(conf, stateStore, null, recvFactory);
    
	  this.collector = new SpoutOutputCollectorMock();
    
	  try {
		  this.spout.preparePartitions(null, totalTasks, taskIndex, new SpoutOutputCollector(this.collector));
	  }
	  catch(Exception ex) {
	  }
  	}
  
  	/**
  	 * Execute a sequence of calls to EventHubSpout.
  	 * 
  	 * @param callSequence: is represented as a string of commands, 
  	 * e.g. "r,r,r,r,a1,f2,...". The commands are:
  	 * r[N]: receive() called N times
  	 * aP_X: ack(P_X), partition: P, offset: X
  	 * fP_Y: fail(P_Y), partition: P, offset: Y
  	 */
  	public String execute(String callSequence) {
  		String[] cmds = callSequence.split(",");
  		for (String cmd : cmds) {
  			if (cmd.startsWith("r")) {
  				int count = 1;
  				if (cmd.length() > 1) {
  					count = Integer.parseInt(cmd.substring(1));
  				}
  				for (int i=0; i<count; ++i) {
  					this.spout.nextTuple();
  				}
  			}
  			else if (cmd.startsWith("a")) {
  				String[] midStrs = cmd.substring(1).split("_");
  				MessageId msgId = new MessageId(midStrs[0], midStrs[1], Long.parseLong(midStrs[1]));
  				this.spout.ack(msgId);
  			}
  			else if (cmd.startsWith("f")) {
  				String[] midStrs = cmd.substring(1).split("_");
  				MessageId msgId = new MessageId(midStrs[0], midStrs[1], Long.parseLong(midStrs[1]));
  				this.spout.fail(msgId);
  			}
  		}
  		return this.collector.getOffsetSequenceAndReset();
  	}
  
  	public String getCheckpoint(int partitionIndex) {
  		String statePath = statePathPrefix + partitionIndex;
  		return this.stateStore.readData(statePath);
  	}
}
