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

import com.microsoft.azure.eventhubs.EventData;
import org.apache.storm.tuple.Fields;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	public List<Object> deserialize(EventData eventData) {
		final List<Object> fieldContents = new ArrayList<Object>();
		String messageData = "";
		if(eventData.getBody()!=null)
			messageData = new String (eventData.getBody(),eventData.getBodyOffset(),eventData.getBodyLength(),Charset.defaultCharset());
		Map metaDataMap = eventData.getProperties();
		fieldContents.add(messageData);
		fieldContents.add(metaDataMap);
		return fieldContents;
	}

	@Override
	public Fields getOutputFields() {
		return new Fields(FieldConstants.Message, FieldConstants.META_DATA);
	}
}
