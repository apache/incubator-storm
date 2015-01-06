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
package org.apache.storm.jdbc.bolt;
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
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic bolt for writing to any Database table.
 * <p/>
 * Note: Each JdbcBolt defined in a topology is tied to a specific table.
 */
public class JdbcBolt extends AbstractJdbcBolt {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcBolt.class);

    boolean writeToWAL = true;

    public JdbcBolt(String tableName, JdbcMapper mapper) {
        super(tableName, mapper);
    }

    public JdbcBolt withConfigKey(String configKey) {
        this.configKey = configKey;
        return this;
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            List<Column> columns = mapper.getColumns(tuple);
            List<List<Column>> columnLists = new ArrayList<List<Column>>();
            columnLists.add(columns);
            this.jdbcClient.insert(this.tableName, columnLists);
        } catch (Exception e) {
            LOG.warn("Failing tuple.", e);
            this.collector.fail(tuple);
            return;
        }

        this.collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
