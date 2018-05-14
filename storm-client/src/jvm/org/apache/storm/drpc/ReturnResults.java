/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.apache.storm.drpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.storm.Config;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.DistributedRPCInvocations;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.ObjectReader;
import org.apache.storm.utils.ServiceRegistry;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReturnResults extends BaseRichBolt {
    public static final Logger LOG = LoggerFactory.getLogger(ReturnResults.class);
    //ANY CHANGE TO THIS CODE MUST BE SERIALIZABLE COMPATIBLE OR THERE WILL BE PROBLEMS
    static final long serialVersionUID = -774882142710631591L;
    OutputCollector _collector;
    boolean local;
    Map<String, Object> _conf;
    Map<List<String>, DRPCInvocationsClient> _clients = new HashMap<>();

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        _conf = topoConf;
        _collector = collector;
        local = topoConf.get(Config.STORM_CLUSTER_MODE).equals("local");
    }

    @Override
    public void execute(Tuple input) {
        String result = (String) input.getValue(0);
        String returnInfo = (String) input.getValue(1);
        LOG.debug("Request Info: {}, Result: {}", returnInfo, result);
        if (returnInfo != null) {
            Map<String, Object> retMap;
            try {
                retMap = (Map<String, Object>) JSONValue.parseWithException(returnInfo);
            } catch (ParseException e) {
                LOG.error("Parseing returnInfo failed", e);
                _collector.fail(input);
                return;
            }
            final String host = (String) retMap.get("host");
            final int port = ObjectReader.getInt(retMap.get("port"));
            String id = (String) retMap.get("id");
            LOG.debug("Request Id: {}, Result: {}", id, result);
            DistributedRPCInvocations.Iface client = getDRPCClient(host, port);

            int retryCnt = 0;
            int maxRetries = 3;
            while (retryCnt < maxRetries) {
                retryCnt++;
                try {
                    LOG.debug("Trying to publish Request Id: {}, Result: {}, to DRPC {}", id, result, host);
                    client.result(id, result);
                    _collector.ack(input);
                    break;
                } catch (AuthorizationException aze) {
                    LOG.error("Not authorized to return results to DRPC server", aze);
                    _collector.fail(input);
                    throw new RuntimeException(aze);
                } catch (TException tex) {
                    if (retryCnt >= maxRetries) {
                        LOG.error("Failed to return results to DRPC server", tex);
                        _collector.fail(input);
                    }
                    client = getDRPCClient(host, port);
                }
            }
        }
    }

    private DistributedRPCInvocations.Iface getDRPCClient(String host, int port) {
        DistributedRPCInvocations.Iface client;
        if (local) {
            client = (DistributedRPCInvocations.Iface) ServiceRegistry.getService(host);
        } else {
            List server = new ArrayList() {
                {
                    add(host);
                    add(port);
                }
            };
            if (!_clients.containsKey(server)) {
                try {
                    _clients.put(server, new DRPCInvocationsClient(_conf, host, port));
                } catch (TTransportException ex) {
                    throw new RuntimeException(ex);
                }
            }
            client = _clients.get(server);
        }
        return client;
    }

    @Override
    public void cleanup() {
        for (DRPCInvocationsClient c : _clients.values()) {
            c.close();
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
