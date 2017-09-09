package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.converter.ClusterStateConverter;
import com.github.igorperikov.heimdallr.converter.ClusterStateDiffConverter;
import com.github.igorperikov.heimdallr.domain.ClusterState;
import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.HeimdallrServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class InterNodeCommunicator {
    // TODO: convert to async stub
    private final HeimdallrServiceGrpc.HeimdallrServiceBlockingStub blockingStub;

    public InterNodeCommunicator(String otherNodeAddress, int otherNodePort) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(otherNodeAddress, otherNodePort)
                .usePlaintext(true);
        ManagedChannel channel = channelBuilder.build();
        blockingStub = HeimdallrServiceGrpc.newBlockingStub(channel);
    }

    public ClusterStateDiff getClusterStateDiff(ClusterState clusterState) {
        ClusterStateTO clusterStateTO = ClusterStateConverter.convertDomain(clusterState);
        ClusterStateDiffTO diffTO = blockingStub.getDiffWithOtherNodesState(clusterStateTO);
        return ClusterStateDiffConverter.convertToDomain(diffTO);
    }
}
