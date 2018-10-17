package org.apache.storm.container.docker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public class DockerRunCommand extends DockerCommand {
    private static final String RUN_COMMAND = "run";
    private final String image;
    private List<String> overrrideCommandWithArgs;

    /**
     * The Construction function.
     * @param executable the docker executable
     * @param containerName the container name
     * @param userInfo the info of the user, e.g. "uid:gid"
     * @param image the container image
     */
    public DockerRunCommand(String executable, String containerName, String userInfo, String image) {
        super(executable, RUN_COMMAND);
        super.addCommandArguments("--name=" + containerName, "--user=" + userInfo);
        this.image = image;
    }

    /**
     * Add --rm option.
     * @return the self
     */
    public DockerRunCommand removeContainerOnExit() {
        super.addCommandArguments("--rm");
        return this;
    }

    /**
     * Add -d option.
     * @return the self
     */
    public DockerRunCommand detachOnRun() {
        super.addCommandArguments("-d");
        return this;
    }

    /**
     * Set --workdir option.
     * @param workdir the working directory
     * @return the self
     */
    public DockerRunCommand setContainerWorkDir(String workdir) {
        super.addCommandArguments("--workdir=" + workdir);
        return this;
    }

    /**
     * Set --net option.
     * @param type the network type
     * @return the self
     */
    public DockerRunCommand setNetworkType(String type) {
        super.addCommandArguments("--net=" + type);
        return this;
    }

    /**
     * Add bind mount locations.
     * @param sourcePath the source path
     * @param destinationPath the destination path
     * @param createSource if createSource is false and the source path doesn't exist, do nothing
     * @return the self
     */
    public DockerRunCommand addMountLocation(String sourcePath, String
        destinationPath, boolean createSource) {
        boolean sourceExists = new File(sourcePath).exists();
        if (!sourceExists && !createSource) {
            return this;
        }
        super.addCommandArguments("-v", sourcePath + ":" + destinationPath);
        return this;
    }

    public DockerRunCommand addReadWriteMountLocation(String sourcePath, String
        destinationPath) {
        super.addCommandArguments("-v", sourcePath + ":" + destinationPath);
        return this;
    }

    /**
     * Add all the rw bind mount locations.
     * @param paths the locations
     * @return the self
     */
    public DockerRunCommand addAllReadWriteMountLocations(List<String> paths) {
        for (String dir: paths) {
            this.addReadWriteMountLocation(dir, dir);
        }
        return this;
    }

    /**
     * Add readonly bind mount location.
     * @param sourcePath the source path
     * @param destinationPath the destination path
     * @param createSource if createSource is false and the source path doesn't exist, do nothing
     * @return the self
     */
    public DockerRunCommand addReadOnlyMountLocation(String sourcePath, String
        destinationPath, boolean createSource) {
        boolean sourceExists = new File(sourcePath).exists();
        if (!sourceExists && !createSource) {
            return this;
        }
        super.addCommandArguments("-v", sourcePath + ":" + destinationPath + ":ro");
        return this;
    }

    /**
     * Add readonly bind mout location.
     * @param sourcePath the source path
     * @param destinationPath the destination path
     * @return the self
     */
    public DockerRunCommand addReadOnlyMountLocation(String sourcePath, String
        destinationPath) {
        super.addCommandArguments("-v", sourcePath + ":" + destinationPath + ":ro");
        return this;
    }

    /**
     * Add all readonly locations.
     * @param paths the locations
     * @return the self
     */
    public DockerRunCommand addAllReadOnlyMountLocations(List<String> paths) {
        for (String dir: paths) {
            this.addReadOnlyMountLocation(dir, dir);
        }
        return this;
    }

    /**
     * Add all readonly locations.
     * @param paths the locations
     * @param createSource if createSource is false and the source path doesn't exist, do nothing
     * @return the self
     */
    public DockerRunCommand addAllReadOnlyMountLocations(List<String> paths,
                                                         boolean createSource) {
        for (String dir: paths) {
            this.addReadOnlyMountLocation(dir, dir, createSource);
        }
        return this;
    }

    /**
     * Set --cgroup-parent option.
     * @param parentPath the cgroup parent path
     * @return the self
     */
    public DockerRunCommand setCGroupParent(String parentPath) {
        super.addCommandArguments("--cgroup-parent=" + parentPath);
        return this;
    }

    /**
     * Set --privileged option to run a privileged container. Use with extreme care.
     * @return the self.
     */
    public DockerRunCommand setPrivileged() {
        super.addCommandArguments("--privileged");
        return this;
    }

    /**
     * Set capabilities of the container.
     * @param capabilities the capabilities to be added
     * @return the self
     */
    public DockerRunCommand setCapabilities(Set<String> capabilities) {
        //first, drop all capabilities
        super.addCommandArguments("--cap-drop=ALL");

        //now, add the capabilities supplied
        for (String capability : capabilities) {
            super.addCommandArguments("--cap-add=" + capability);
        }

        return this;
    }

    /**
     * Set --device option.
     * @param sourceDevice the source device
     * @param destinationDevice the destination device
     * @return the self
     */
    public DockerRunCommand addDevice(String sourceDevice, String destinationDevice) {
        super.addCommandArguments("--device=" + sourceDevice + ":" + destinationDevice);
        return this;
    }

    /**
     * Enable detach.
     * @return the self
     */
    public DockerRunCommand enableDetach() {
        super.addCommandArguments("--detach=true");
        return this;
    }

    /**
     * Disable detach.
     * @return the self
     */
    public DockerRunCommand disableDetach() {
        super.addCommandArguments("--detach=false");
        return this;
    }

    /**
     * Set --group-add option.
     * @param groups the groups to be added
     * @return the self
     */
    public DockerRunCommand groupAdd(String[] groups) {
        for (int i = 0; i < groups.length; i++) {
            super.addCommandArguments("--group-add " + groups[i]);
        }
        return this;
    }

    /**
     * Set extra commands and args. It can override the existing commands.
     * @param overrideCommandWithArgs the extra commands and args
     * @return the self
     */
    public DockerRunCommand setOverrideCommandWithArgs(
        List<String> overrideCommandWithArgs) {
        this.overrrideCommandWithArgs = overrideCommandWithArgs;
        return this;
    }

    /**
     * Add --read-only option.
     * @return the self
     */
    public DockerRunCommand setReadonly() {
        super.addCommandArguments("--read-only");
        return this;
    }

    /**
     * Set --security-opt option.
     * @param jsonPath the path to the json file
     * @return the self
     */
    public DockerRunCommand setSeccompProfile(String jsonPath) {
        super.addCommandArguments("--security-opt seccomp=" + jsonPath);
        return this;
    }

    /**
     * Set no-new-privileges option.
     * @return the self
     */
    public DockerRunCommand setNoNewPrivileges() {
        super.addCommandArguments("--security-opt no-new-privileges");
        return this;
    }

    /**
     * Set cpuShares.
     * @param cpuShares the cpu shares
     * @return the self
     */
    public DockerRunCommand setCpuShares(int cpuShares) {
        // Zero sets to default of 1024.  2 is the minimum value otherwise
        if (cpuShares < 2) {
            cpuShares = 2;
        }
        super.addCommandArguments("--cpu-shares=" + String.valueOf(cpuShares));
        return this;
    }

    /**
     * Set the number of cpus to use.
     * @param cpus the number of cpus
     * @return the self
     */
    public DockerRunCommand setCpus(double cpus) {
        super.addCommandArguments("--cpus=" + cpus);
        return this;
    }

    /**
     * Set the number of memory in MB to use.
     * @param memoryMb the number of memory in MB
     * @return the self
     */
    public DockerRunCommand setMemoryMb(int memoryMb) {
        super.addCommandArguments("--memory=" + memoryMb + "m");
        return this;
    }

    /**
     * Set the output container id file location.
     * @param cidFile the container id file
     * @return the self
     */
    public DockerRunCommand setCidFile(String cidFile) {
        super.addCommandArguments("--cidfile=" + cidFile);
        return this;
    }

    /**
     * Get the full command.
     * @return the full command
     */
    @Override
    public String getCommandWithArguments() {
        List<String> argList = new ArrayList<>();

        argList.add(super.getCommandWithArguments());
        argList.add(image);

        if (overrrideCommandWithArgs != null) {
            argList.addAll(overrrideCommandWithArgs);
        }

        return StringUtils.join(argList, " ");
    }
}
