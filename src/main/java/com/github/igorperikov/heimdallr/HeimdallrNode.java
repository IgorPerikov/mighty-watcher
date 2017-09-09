package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.domain.ClusterState;
import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.epidemics.AntiEntropyMechanism;
import com.github.igorperikov.heimdallr.epidemics.RandomNodeAntiEntropyMechanism;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class HeimdallrNode {
    private final int port;

    @Getter
    private final UUID label;

    @Getter
    private ClusterState clusterState;

    private String peerNodeAddress;
    private Integer peerNodePort;

    public HeimdallrNode(int port) {
        this.port = port;
        this.label = UUID.randomUUID();
    }

    public HeimdallrNode(int port, String peerAddress, int peerPort) {
        this(port);
        this.peerNodeAddress = peerAddress;
        this.peerNodePort = peerPort;
    }

    public void start() {
        initOwnClusterState();

        Server server = ServerBuilder.forPort(port)
                .addService(new HeimdallrServiceImplementation(this))
                .build();

        if (peerNodeAddress != null && peerNodePort != null) {
            log.info("Peer node {}:{} is provided", peerNodeAddress, peerNodePort);
            ClusterStateDiff diff = new InterNodeCommunicator(peerNodeAddress, peerNodePort)
                    .getClusterStateDiff(getClusterState());
            clusterState.applyDiff(diff);
        }

        try {
            server.start();
            log.info("{} start and listening on {}", label, port);
        } catch (IOException e) {
            log.error("", e);
            System.exit(1);
        }

        AntiEntropyMechanism antiEntropyMechanism = new RandomNodeAntiEntropyMechanism(this);
        antiEntropyMechanism.launch();

        new ClusterInfoLogger(this).startPrintingClusterInfo();

        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    private void initOwnClusterState() {
        clusterState = new ClusterState(label.toString(), getNodeAddress());
    }

    public String getNodeAddress() {
        // TODO: localhost -> ip detection
        return "localhost:" + port;
    }

    public NodeDefinition getNodeDefinition() {
        return clusterState.getNodes().get(label.toString());
    }
}
