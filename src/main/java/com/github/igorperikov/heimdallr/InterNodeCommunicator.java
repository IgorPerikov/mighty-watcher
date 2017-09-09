package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import com.github.igorperikov.heimdallr.generated.HeimdallrServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class InterNodeCommunicator {
    public ClusterStateDiffTO getClusterStateDiff(HeimdallrNode node, String otherNodeAddress, int otherNodePort) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(otherNodeAddress, otherNodePort)
                .usePlaintext(true);
        ManagedChannel channel = channelBuilder.build();
        HeimdallrServiceGrpc.HeimdallrServiceBlockingStub call = HeimdallrServiceGrpc.newBlockingStub(channel);
        return call.getDiffWithOtherNodesState(node.getClusterState());
    }
}
