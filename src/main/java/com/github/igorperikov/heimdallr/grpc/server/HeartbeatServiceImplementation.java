package com.github.igorperikov.heimdallr.grpc.server;

import com.github.igorperikov.heimdallr.generated.HeartbeatRequest;
import com.github.igorperikov.heimdallr.generated.HeartbeatResponse;
import com.github.igorperikov.heimdallr.generated.HeartbeatServiceGrpc;
import io.grpc.stub.StreamObserver;

public class HeartbeatServiceImplementation extends HeartbeatServiceGrpc.HeartbeatServiceImplBase {
    @Override
    public void heartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        responseObserver.onNext(HeartbeatResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
