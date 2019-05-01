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
package org.apache.storm.eventhubs.format;

import org.apache.storm.eventhubs.core.EventHubMessage;
import org.apache.storm.eventhubs.core.FieldConstants;
import org.apache.storm.tuple.Fields;

import java.util.ArrayList;
import java.util.List;

/**
 * An Event Data Scheme which deserializes message payload into the Strings. No
 * encoding is assumed. The receiver will need to handle parsing of the string
 * data in appropriate encoding.
 *
 * The resulting tuple would contain two items: the the message string, and a
 * map of properties that include metadata, which can be used to determine who
 * processes the message, and how it is processed.
 *
 * For passing the raw bytes of a messsage to Bolts, refer to
 * {@link BinaryEventDataScheme}.
 */
public class EventDataScheme implements IEventDataScheme {
	private static final long serialVersionUID = 1L;
	
	@Override
	public List<Object> deserialize(EventHubMessage eventHubMessage) {
		final List<Object> fieldContents = new ArrayList<Object>();
		String messageData = new String(eventHubMessage.getContent());
		fieldContents.add(messageData);
		fieldContents.add(eventHubMessage.getApplicationProperties());
		return fieldContents;
	}

	@Override
	public Fields getOutputFields() {
		return new Fields(FieldConstants.MESSAGE_FIELD, FieldConstants.META_DATA_FIELD);
	}
}
