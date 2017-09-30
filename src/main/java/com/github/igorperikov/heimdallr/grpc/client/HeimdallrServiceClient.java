package com.github.igorperikov.heimdallr.grpc.client;

import com.github.igorperikov.heimdallr.converter.ClusterStateConverter;
import com.github.igorperikov.heimdallr.converter.ClusterStateDiffConverter;
import com.github.igorperikov.heimdallr.domain.ClusterState;
import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.HeimdallrServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HeimdallrServiceClient {
    public ClusterStateDiff getClusterStateDiff(
            ClusterState clusterState,
            String otherNodeAddress,
            int otherNodePort
    ) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(otherNodeAddress, otherNodePort)
                .usePlaintext(true);
        ManagedChannel channel = channelBuilder.build();
        ClusterStateTO clusterStateTO = ClusterStateConverter.convertDomain(clusterState);
        return ClusterStateDiffConverter.convertToDomain(
                HeimdallrServiceGrpc.newBlockingStub(channel)
                        .getDiffWithOtherNodesState(clusterStateTO)
        );
    }
}
