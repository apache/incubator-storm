package org.apache.storm.daemon.supervisor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.container.ResourceIsolationInterface;
import org.apache.storm.generated.LocalAssignment;
import org.apache.storm.generated.ProfileAction;
import org.apache.storm.generated.ProfileRequest;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.utils.LocalState;
import org.apache.storm.utils.Utils;
import org.junit.Test;

public class BasicContainerTest {
    public static class CommandRun {
        final List<String> cmd;
        final Map<String, String> env;
        final File pwd;
        
        public CommandRun(List<String> cmd, Map<String, String> env, File pwd) {
            this.cmd = cmd;
            this.env = env;
            this.pwd = pwd;
        }
    }
    
    public static class MockBasicContainer extends BasicContainer {
        public final List<CommandRun> profileCmds = new ArrayList<>();
        public final List<CommandRun> workerCmds = new ArrayList<>();
        
        public MockBasicContainer(int port, LocalAssignment assignment, Map<String, Object> conf,
                String supervisorId, LocalState localState, ResourceIsolationInterface resourceIsolationManager,
                boolean recover) throws IOException {
            super(port, assignment, conf, supervisorId, localState, resourceIsolationManager, recover);
        }
        
        public MockBasicContainer(AdvancedFSOps ops, int port, LocalAssignment assignment,
                Map<String, Object> conf, Map<String, Object> topoConf, String supervisorId, 
                ResourceIsolationInterface resourceIsolationManager, LocalState localState,
                String profileCmd) throws IOException {
            super(ops, port, assignment, conf, topoConf, supervisorId, resourceIsolationManager, localState, profileCmd);
        }
        
        @Override
        protected Map<String, Object> readTopoConf() throws IOException {
            return new HashMap<>();
        }
        
        @Override
        public void createNewWorkerId() {
            super.createNewWorkerId();
        }
        
        @Override
        public List<String> substituteChildopts(Object value, int memOnheap) {
            return super.substituteChildopts(value, memOnheap);
        }
               
        @Override
        protected boolean runProfilingCommand(List<String> command, Map<String, String> env, String logPrefix,
                File targetDir) throws IOException, InterruptedException {
            profileCmds.add(new CommandRun(command, env, targetDir));
            return true;
        }
        
        @Override
        protected void launchWorkerProcess(List<String> command, Map<String, String> env, String logPrefix,
                ExitCodeCallback processExitCallback, File targetDir) throws IOException {
            workerCmds.add(new CommandRun(command, env, targetDir));
        }
        
        @Override
        protected String javaCmd(String cmd) {
            //avoid system dependent things
            return cmd;
        }
        
        @Override
        protected List<String> frameworkClasspath() {
            //We are not really running anything so make this
            // simple to check for
            return Arrays.asList("FRAMEWORK_CP");
        }
        
        @Override
        protected String javaLibraryPath(String stormRoot, Map<String, Object> conf) {
            return "JLP";
        }
    }
    
    @Test
    public void testCreateNewWorkerId() throws Exception {
        final String topoId = "test_topology";
        final int port = 8080;
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        
        AdvancedFSOps ops = mock(AdvancedFSOps.class);
        
        LocalState ls = mock(LocalState.class);
        
        MockBasicContainer mc = new MockBasicContainer(ops, port, la, new HashMap<String, Object>(), 
                new HashMap<String, Object>(), "SUPERVISOR", null, ls, "profile");
        
        mc.createNewWorkerId();
        
        assertNotNull(mc._workerId);
        verify(ls).getApprovedWorkers();
        Map<String, Integer> expectedNewState = new HashMap<String, Integer>();
        expectedNewState.put(mc._workerId, port);
        verify(ls).setApprovedWorkers(expectedNewState);
    }
    
    @Test
    public void testRecovery() throws Exception {
        final String topoId = "test_topology";
        final String workerId = "myWorker";
        final int port = 8080;
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        
        Map<String, Integer> workerState = new HashMap<String, Integer>();
        workerState.put(workerId, port);
        
        LocalState ls = mock(LocalState.class);
        when(ls.getApprovedWorkers()).thenReturn(workerState);
        
        MockBasicContainer mc = new MockBasicContainer(port, la, new HashMap<String, Object>(), 
                "SUPERVISOR", ls, null, true);
        
        assertEquals(workerId, mc._workerId);
    }
    
    @Test
    public void testRecoveryMiss() throws Exception {
        final String topoId = "test_topology";
        final int port = 8080;
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        
        Map<String, Integer> workerState = new HashMap<String, Integer>();
        workerState.put("somethingelse", port+1);
        
        LocalState ls = mock(LocalState.class);
        when(ls.getApprovedWorkers()).thenReturn(workerState);
        
        try {
            new MockBasicContainer(port, la, new HashMap<String, Object>(), 
                    "SUPERVISOR", ls, null, true);
            fail("Container recovered worker incorrectly");
        } catch (ContainerRecoveryException e) {
            //Expected
        }
    }
    
    @Test
    public void testCleanUp() throws Exception {
        final String topoId = "test_topology";
        final int port = 8080;
        final String workerId = "worker-id";
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        
        AdvancedFSOps ops = mock(AdvancedFSOps.class);
        
        Map<String, Integer> workerState = new HashMap<String, Integer>();
        workerState.put(workerId, port);
        
        LocalState ls = mock(LocalState.class);
        when(ls.getApprovedWorkers()).thenReturn(new HashMap<>(workerState));
        
        MockBasicContainer mc = new MockBasicContainer(ops, port, la, new HashMap<String, Object>(), 
                new HashMap<String, Object>(), "SUPERVISOR", null, ls, "profile");
        mc._workerId = workerId;
        
        mc.cleanUp();
        
        assertNull(mc._workerId);
        verify(ls).getApprovedWorkers();
        Map<String, Integer> expectedNewState = new HashMap<String, Integer>();
        verify(ls).setApprovedWorkers(expectedNewState);
    }
    
    @Test
    public void testRunProfiling() throws Exception {
        final long pid = 100;
        final String topoId = "test_topology";
        final int port = 8080;
        final String workerId = "worker-id";
        final String stormLocal = ContainerTest.asAbsPath("tmp", "testing");
        final String topoRoot = ContainerTest.asAbsPath(stormLocal, topoId, String.valueOf(port));
        final File workerArtifactsPid = ContainerTest.asAbsFile(topoRoot, "worker.pid");
        
        final Map<String, Object> superConf = new HashMap<>();
        superConf.put(Config.STORM_LOCAL_DIR, stormLocal);
        superConf.put(Config.STORM_WORKERS_ARTIFACTS_DIR, stormLocal);
        
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        
        AdvancedFSOps ops = mock(AdvancedFSOps.class);
        when(ops.slurpString(workerArtifactsPid)).thenReturn(String.valueOf(pid));
        
        LocalState ls = mock(LocalState.class);
        
        MockBasicContainer mc = new MockBasicContainer(ops, port, la, superConf, 
                new HashMap<String, Object>(), "SUPERVISOR", null, ls, "profile");
        mc._workerId = workerId;
        
        //HEAP DUMP
        ProfileRequest req = new ProfileRequest();
        req.set_action(ProfileAction.JMAP_DUMP);
        
        mc.runProfiling(req, false);
        
        assertEquals(1, mc.profileCmds.size());
        CommandRun cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        assertEquals(Arrays.asList("profile", String.valueOf(pid), "jmap", topoRoot), cmd.cmd);
        assertEquals(new File(topoRoot), cmd.pwd);
        
        //JSTACK DUMP
        req.set_action(ProfileAction.JSTACK_DUMP);
        
        mc.runProfiling(req, false);
        
        assertEquals(1, mc.profileCmds.size());
        cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        assertEquals(Arrays.asList("profile", String.valueOf(pid), "jstack", topoRoot), cmd.cmd);
        assertEquals(new File(topoRoot), cmd.pwd);
        
        //RESTART
        req.set_action(ProfileAction.JVM_RESTART);
        
        mc.runProfiling(req, false);
        
        assertEquals(1, mc.profileCmds.size());
        cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        assertEquals(Arrays.asList("profile", String.valueOf(pid), "kill"), cmd.cmd);
        assertEquals(new File(topoRoot), cmd.pwd);
        
        //JPROFILE DUMP
        req.set_action(ProfileAction.JPROFILE_DUMP);
        
        mc.runProfiling(req, false);
        
        assertEquals(1, mc.profileCmds.size());
        cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        assertEquals(Arrays.asList("profile", String.valueOf(pid), "dump", topoRoot), cmd.cmd);
        assertEquals(new File(topoRoot), cmd.pwd);
        
        //JPROFILE START
        req.set_action(ProfileAction.JPROFILE_STOP);
        
        mc.runProfiling(req, false);
        
        assertEquals(1, mc.profileCmds.size());
        cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        assertEquals(Arrays.asList("profile", String.valueOf(pid), "start"), cmd.cmd);
        assertEquals(new File(topoRoot), cmd.pwd);
        
        //JPROFILE STOP
        req.set_action(ProfileAction.JPROFILE_STOP);
        
        mc.runProfiling(req, true);
        
        assertEquals(1, mc.profileCmds.size());
        cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        assertEquals(Arrays.asList("profile", String.valueOf(pid), "stop", topoRoot), cmd.cmd);
        assertEquals(new File(topoRoot), cmd.pwd);
    }
    
    private static void setSystemProp(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }
    
    private static interface Run {
        public void run() throws Exception;
    }
    
    private static void checkpoint(Run r, String ... newValues) throws Exception {
        if (newValues.length % 2 != 0) {
            throw new IllegalArgumentException("Parameters are of the form system property name, new value");
        }
        Map<String, String> orig = new HashMap<>();
        try {
            for (int index = 0; index < newValues.length; index += 2) {
                String key = newValues[index];
                String value = newValues[index + 1];
                orig.put(key, System.getProperty(key));
                setSystemProp(key, value);
            }
            r.run();
        } finally {
            for (Map.Entry<String, String> entry: orig.entrySet()) {
                setSystemProp(entry.getKey(), entry.getValue());
            }
        }
    }
    
    private static <T> void assertListEquals(List<T> a, List<T> b) {
        if (a == null) {
            assertNull(b);
        }
        if (b == null) {
            assertNull(a);
        }
        int commonLen = Math.min(a.size(), b.size());
        for (int i = 0; i < commonLen; i++) {
            assertEquals("at index "+i+"\n"+a+" !=\n"+b+"\n", a.get(i), b.get(i));
        }
        
        assertEquals("size of lists don't match \n"+a+" !=\n"+b, a.size(), b.size());
    }
    
    @Test
    public void testLaunch() throws Exception {
        final String topoId = "test_topology";
        final int port = 8080;
        final String stormHome = ContainerTest.asAbsPath("tmp", "storm-home");
        final String stormLogDir = ContainerTest.asFile(".", "target").getCanonicalPath();
        final String workerId = "worker-id";
        final String stormLocal = ContainerTest.asAbsPath("tmp", "storm-local");
        final String distRoot = ContainerTest.asAbsPath(stormLocal, "supervisor", "stormdist", topoId);
        final File stormcode = new File(distRoot, "stormcode.ser");
        final File stormjar = new File(distRoot, "stormjar.jar");
        final String log4jdir = ContainerTest.asAbsPath(stormHome, "conf");
        final String workerConf = ContainerTest.asAbsPath(log4jdir, "worker.xml");
        final String workerRoot = ContainerTest.asAbsPath(stormLocal, "workers", workerId);
        final String workerTmpDir = ContainerTest.asAbsPath(workerRoot, "tmp");
        
        final StormTopology st = new StormTopology();
        st.set_spouts(new HashMap<>());
        st.set_bolts(new HashMap<>());
        st.set_state_spouts(new HashMap<>());
        byte [] serializedState = Utils.gzip(Utils.thriftSerialize(st));
        
        final Map<String, Object> superConf = new HashMap<>();
        superConf.put(Config.STORM_LOCAL_DIR, stormLocal);
        superConf.put(Config.STORM_WORKERS_ARTIFACTS_DIR, stormLocal);
        superConf.put(Config.STORM_LOG4J2_CONF_DIR, log4jdir);
        
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        
        AdvancedFSOps ops = mock(AdvancedFSOps.class);
        when(ops.slurp(stormcode)).thenReturn(serializedState);
        
        LocalState ls = mock(LocalState.class);
        
        MockBasicContainer mc = new MockBasicContainer(ops, port, la, superConf, 
                new HashMap<String, Object>(), "SUPERVISOR", null, ls, "profile");
        mc._workerId = workerId;
        
        checkpoint(() -> mc.launch(), 
                "storm.home", stormHome,
                "storm.log.dir", stormLogDir);

        assertEquals(1, mc.workerCmds.size());
        CommandRun cmd = mc.workerCmds.get(0);
        mc.workerCmds.clear();
        assertListEquals(Arrays.asList(
                "java",
                "-cp",
                "FRAMEWORK_CP:" + stormjar.getAbsolutePath(),
                "-Dlogging.sensitivity=S3",
                "-Dlogfile.name=worker.log",
                "-Dstorm.home=" + stormHome,
                "-Dworkers.artifacts=" + stormLocal,
                "-Dstorm.id=" + topoId,
                "-Dworker.id=" + workerId,
                "-Dworker.port=" + port,
                "-Dstorm.log.dir=" + stormLogDir,
                "-Dlog4j.configurationFile=" + workerConf,
                "-DLog4jContextSelector=org.apache.logging.log4j.core.selector.BasicContextSelector",
                "-Dstorm.local.dir=" + stormLocal,
                "org.apache.storm.LogWriter",
                "java",
                "-server",
                "-Dlogging.sensitivity=S3",
                "-Dlogfile.name=worker.log",
                "-Dstorm.home=" + stormHome,
                "-Dworkers.artifacts=" + stormLocal,
                "-Dstorm.id=" + topoId,
                "-Dworker.id=" + workerId,
                "-Dworker.port=" + port,
                "-Dstorm.log.dir=" + stormLogDir,
                "-Dlog4j.configurationFile=" + workerConf,
                "-DLog4jContextSelector=org.apache.logging.log4j.core.selector.BasicContextSelector",
                "-Dstorm.local.dir=" + stormLocal,
                "-Djava.library.path=JLP",
                "-Dstorm.conf.file=",
                "-Dstorm.options=",
                "-Djava.io.tmpdir="+workerTmpDir,
                "-cp",
                "FRAMEWORK_CP:" + stormjar.getAbsolutePath(),
                "org.apache.storm.daemon.worker", 
                topoId, 
                "SUPERVISOR",
                String.valueOf(port),
                workerId
                ), cmd.cmd);
        assertEquals(new File(workerRoot), cmd.pwd);
    }
    
    @Test
    public void testSubstChildOpts() throws Exception {
        String workerId = "w-01";
        String topoId = "s-01";
        int port = 9999;
        int memOnheap = 512;
        
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        
        AdvancedFSOps ops = mock(AdvancedFSOps.class);
        
        LocalState ls = mock(LocalState.class);
        
        MockBasicContainer mc = new MockBasicContainer(ops, port, la, new HashMap<String, Object>(), 
                new HashMap<String, Object>(), "SUPERVISOR", null, ls, "profile");
        mc._workerId = workerId;
        
        assertListEquals(Arrays.asList(
                "-Xloggc:/tmp/storm/logs/gc.worker-9999-s-01-w-01-9999.log",
                "-Xms256m",
                "-Xmx512m"),
                mc.substituteChildopts("-Xloggc:/tmp/storm/logs/gc.worker-%ID%-%TOPOLOGY-ID%-%WORKER-ID%-%WORKER-PORT%.log -Xms256m -Xmx%HEAP-MEM%m", memOnheap));
        
        assertListEquals(Arrays.asList(
                "-Xloggc:/tmp/storm/logs/gc.worker-9999-s-01-w-01-9999.log",
                "-Xms256m",
                "-Xmx512m"),
                mc.substituteChildopts(Arrays.asList("-Xloggc:/tmp/storm/logs/gc.worker-%ID%-%TOPOLOGY-ID%-%WORKER-ID%-%WORKER-PORT%.log","-Xms256m","-Xmx%HEAP-MEM%m"), memOnheap));
        
        assertListEquals(Collections.emptyList(), 
                mc.substituteChildopts(null));
    }
}
