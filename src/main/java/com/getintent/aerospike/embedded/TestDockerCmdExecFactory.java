package com.getintent.aerospike.embedded;


import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.command.AuthCmd.Exec;
import com.github.dockerjava.core.DockerClientConfig;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Special {@link DockerCmdExecFactory} implementation that collects container and image creations while test execution
 * for the purpose of automatically cleanup.
 */
public class TestDockerCmdExecFactory implements DockerCmdExecFactory {

    private List<String> containerNames = new ArrayList<>();

    private List<String> imageNames = new ArrayList<>();

    private DockerCmdExecFactory delegate;

    public TestDockerCmdExecFactory(DockerCmdExecFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public void init(DockerClientConfig dockerClientConfig) {
        delegate.init(dockerClientConfig);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public CreateContainerCmd.Exec createCreateContainerCmdExec() {
        return command -> {
            CreateContainerResponse createContainerResponse = delegate.createCreateContainerCmdExec().exec(command);
            containerNames.add(createContainerResponse.getId());
            return createContainerResponse;
        };
    }

    @Override
    public RemoveContainerCmd.Exec createRemoveContainerCmdExec() {
        return command -> {
            delegate.createRemoveContainerCmdExec().exec(command);
            containerNames.remove(command.getContainerId());
            return null;
        };
    }

    @Override
    public CreateImageCmd.Exec createCreateImageCmdExec() {
        return command -> {
            CreateImageResponse createImageResponse = delegate.createCreateImageCmdExec().exec(command);
            imageNames.add(createImageResponse.getId());
            return createImageResponse;
        };
    }

    @Override
    public RemoveImageCmd.Exec createRemoveImageCmdExec() {
        return command -> {
            delegate.createRemoveImageCmdExec().exec(command);
            imageNames.remove(command.getImageId());
            return null;
        };
    }

    @Override
    public BuildImageCmd.Exec createBuildImageCmdExec() {
        return (command, resultCallback) -> {
            // can't detect image id here so tagging it
            String tag = command.getTag();
            if (tag == null || "".equals(tag.trim())) {
                tag = "" + new SecureRandom().nextInt(Integer.MAX_VALUE);
                command.withTag(tag);
            }
            delegate.createBuildImageCmdExec().exec(command, resultCallback);
            imageNames.add(tag);
            return null;
        };
    }

    @Override
    public Exec createAuthCmdExec() {
        return delegate.createAuthCmdExec();
    }

    @Override
    public InfoCmd.Exec createInfoCmdExec() {
        return delegate.createInfoCmdExec();
    }

    @Override
    public PingCmd.Exec createPingCmdExec() {
        return delegate.createPingCmdExec();
    }

    @Override
    public ExecCreateCmd.Exec createExecCmdExec() {
        return delegate.createExecCmdExec();
    }

    @Override
    public VersionCmd.Exec createVersionCmdExec() {
        return delegate.createVersionCmdExec();
    }

    @Override
    public PullImageCmd.Exec createPullImageCmdExec() {
        return delegate.createPullImageCmdExec();
    }

    @Override
    public PushImageCmd.Exec createPushImageCmdExec() {
        return delegate.createPushImageCmdExec();
    }

    @Override
    public SaveImageCmd.Exec createSaveImageCmdExec() {
        return delegate.createSaveImageCmdExec();
    }

    @Override
    public SearchImagesCmd.Exec createSearchImagesCmdExec() {
        return delegate.createSearchImagesCmdExec();
    }

    @Override
    public ListImagesCmd.Exec createListImagesCmdExec() {
        return delegate.createListImagesCmdExec();
    }

    @Override
    public InspectImageCmd.Exec createInspectImageCmdExec() {
        return delegate.createInspectImageCmdExec();
    }

    @Override
    public ListContainersCmd.Exec createListContainersCmdExec() {
        return delegate.createListContainersCmdExec();
    }

    @Override
    public StartContainerCmd.Exec createStartContainerCmdExec() {
        return delegate.createStartContainerCmdExec();
    }

    @Override
    public InspectContainerCmd.Exec createInspectContainerCmdExec() {
        return delegate.createInspectContainerCmdExec();
    }

    @Override
    public WaitContainerCmd.Exec createWaitContainerCmdExec() {
        return delegate.createWaitContainerCmdExec();
    }

    @Override
    public AttachContainerCmd.Exec createAttachContainerCmdExec() {
        return delegate.createAttachContainerCmdExec();
    }

    @Override
    public ExecStartCmd.Exec createExecStartCmdExec() {
        return delegate.createExecStartCmdExec();
    }

    @Override
    public InspectExecCmd.Exec createInspectExecCmdExec() {
        return delegate.createInspectExecCmdExec();
    }

    @Override
    public LogContainerCmd.Exec createLogContainerCmdExec() {
        return delegate.createLogContainerCmdExec();
    }

    @Override
    public CopyFileFromContainerCmd.Exec createCopyFileFromContainerCmdExec() {
        return delegate.createCopyFileFromContainerCmdExec();
    }

    @Override
    public CopyArchiveFromContainerCmd.Exec createCopyArchiveFromContainerCmdExec() {
        return delegate.createCopyArchiveFromContainerCmdExec();
    }

    @Override
    public CopyArchiveToContainerCmd.Exec createCopyArchiveToContainerCmdExec() {
        return delegate.createCopyArchiveToContainerCmdExec();
    }

    @Override
    public StopContainerCmd.Exec createStopContainerCmdExec() {
        return delegate.createStopContainerCmdExec();
    }

    @Override
    public ContainerDiffCmd.Exec createContainerDiffCmdExec() {
        return delegate.createContainerDiffCmdExec();
    }

    @Override
    public KillContainerCmd.Exec createKillContainerCmdExec() {
        return delegate.createKillContainerCmdExec();
    }

    @Override
    public UpdateContainerCmd.Exec createUpdateContainerCmdExec() {
        return delegate.createUpdateContainerCmdExec();
    }

    @Override
    public RenameContainerCmd.Exec createRenameContainerCmdExec() {
        return delegate.createRenameContainerCmdExec();
    }

    @Override
    public RestartContainerCmd.Exec createRestartContainerCmdExec() {
        return delegate.createRestartContainerCmdExec();
    }

    @Override
    public CommitCmd.Exec createCommitCmdExec() {
        return delegate.createCommitCmdExec();
    }

    @Override
    public TopContainerCmd.Exec createTopContainerCmdExec() {
        return delegate.createTopContainerCmdExec();
    }

    @Override
    public TagImageCmd.Exec createTagImageCmdExec() {
        return delegate.createTagImageCmdExec();
    }

    @Override
    public PauseContainerCmd.Exec createPauseContainerCmdExec() {
        return delegate.createPauseContainerCmdExec();
    }

    @Override
    public UnpauseContainerCmd.Exec createUnpauseContainerCmdExec() {
        return delegate.createUnpauseContainerCmdExec();
    }

    @Override
    public EventsCmd.Exec createEventsCmdExec() {
        return delegate.createEventsCmdExec();
    }

    @Override
    public StatsCmd.Exec createStatsCmdExec() {
        return delegate.createStatsCmdExec();
    }

    @Override
    public CreateVolumeCmd.Exec createCreateVolumeCmdExec() {
        return delegate.createCreateVolumeCmdExec();
    }

    @Override
    public InspectVolumeCmd.Exec createInspectVolumeCmdExec() {
        return delegate.createInspectVolumeCmdExec();
    }

    @Override
    public RemoveVolumeCmd.Exec createRemoveVolumeCmdExec() {
        return delegate.createRemoveVolumeCmdExec();
    }

    @Override
    public ListVolumesCmd.Exec createListVolumesCmdExec() {
        return delegate.createListVolumesCmdExec();
    }

    @Override
    public ListNetworksCmd.Exec createListNetworksCmdExec() {
        return delegate.createListNetworksCmdExec();
    }

    @Override
    public InspectNetworkCmd.Exec createInspectNetworkCmdExec() {
        return delegate.createInspectNetworkCmdExec();
    }

    @Override
    public CreateNetworkCmd.Exec createCreateNetworkCmdExec() {
        return delegate.createCreateNetworkCmdExec();
    }

    @Override
    public RemoveNetworkCmd.Exec createRemoveNetworkCmdExec() {
        return delegate.createRemoveNetworkCmdExec();
    }

    @Override
    public ConnectToNetworkCmd.Exec createConnectToNetworkCmdExec() {
        return delegate.createConnectToNetworkCmdExec();
    }

    @Override
    public DisconnectFromNetworkCmd.Exec createDisconnectFromNetworkCmdExec() {
        return delegate.createDisconnectFromNetworkCmdExec();
    }

    @Override
    public DockerCmdExecFactory withSSLContext(SSLContext sslContext) {
        return delegate.withSSLContext(sslContext);
    }

    public List<String> getContainerNames() {
        return new ArrayList<>(containerNames);
    }

}