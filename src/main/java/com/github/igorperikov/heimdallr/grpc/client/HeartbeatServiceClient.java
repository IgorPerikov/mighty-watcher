package com.github.igorperikov.heimdallr.grpc.client;

import com.github.igorperikov.heimdallr.generated.HeartbeatRequest;
import com.github.igorperikov.heimdallr.generated.HeartbeatServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class HeartbeatServiceClient {
    public void call(String otherNodeAddress, int otherNodePort) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(otherNodeAddress, otherNodePort)
                .usePlaintext(true);
        ManagedChannel channel = channelBuilder.build();
        HeartbeatServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(750, TimeUnit.MILLISECONDS)
                .heartbeat(HeartbeatRequest.newBuilder().build());
    }
}
