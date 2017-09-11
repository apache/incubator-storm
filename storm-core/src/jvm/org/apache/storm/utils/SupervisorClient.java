package org.apache.storm.utils;

import org.apache.storm.Config;
import org.apache.storm.generated.Supervisor;
import org.apache.storm.security.auth.ThriftClient;
import org.apache.storm.security.auth.ThriftConnectionType;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SupervisorClient extends ThriftClient {
    private Supervisor.Client _client;
    private static final Logger LOG = LoggerFactory.getLogger(SupervisorClient.class);

    public static SupervisorClient getConfiguredClient(Map conf, String host) {
        return getConfiguredClientAs(conf, host, null);
    }

    public static SupervisorClient getConfiguredClientAs(Map conf, String host, String asUser) {
        if (conf.containsKey(Config.STORM_DO_AS_USER)) {
            if (asUser != null && !asUser.isEmpty()) {
                LOG.warn("You have specified a doAsUser as param {} and a doAsParam as config, config will take precedence."
                        , asUser, conf.get(Config.STORM_DO_AS_USER));
            }
            asUser = (String) conf.get(Config.STORM_DO_AS_USER);
        }
        int port = Integer.parseInt(conf.get(Config.SUPERVISOR_THRIFT_PORT).toString());
        try {
            return new SupervisorClient(conf, host, port, null, asUser);
        } catch (TTransportException e) {
            throw new RuntimeException("Failed to create a supervisor client for host " + host);
        }
    }

    public SupervisorClient(Map conf, String host, int port) throws TTransportException {
        this(conf, host, port, null, null);
    }

    public SupervisorClient(Map conf, String host, int port, Integer timeout) throws TTransportException {
        super(conf, ThriftConnectionType.SUPERVISOR, host, port, timeout, null);
        _client = new Supervisor.Client(_protocol);
    }

    public SupervisorClient(Map conf, String host, Integer port, Integer timeout, String asUser) throws TTransportException {
        super(conf, ThriftConnectionType.SUPERVISOR, host, port, timeout, asUser);
        _client = new Supervisor.Client(_protocol);
    }

    public SupervisorClient(Map conf, String host) throws TTransportException {
        super(conf, ThriftConnectionType.SUPERVISOR, host, null, null, null);
        _client = new Supervisor.Client(_protocol);
    }

    public Supervisor.Client getClient() {
        return _client;
    }
}
