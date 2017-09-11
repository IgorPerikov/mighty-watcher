package com.github.igorperikov.heimdallr.generated;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.5.0)",
    comments = "Source: heimdallr/heimdallr-service.proto")
public final class HeartbeatServiceGrpc {

  private HeartbeatServiceGrpc() {}

  public static final String SERVICE_NAME = "heimdallr.HeartbeatService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.github.igorperikov.heimdallr.generated.HeartbeatRequest,
      com.github.igorperikov.heimdallr.generated.HeartbeatResponse> METHOD_HEARTBEAT =
      io.grpc.MethodDescriptor.<com.github.igorperikov.heimdallr.generated.HeartbeatRequest, com.github.igorperikov.heimdallr.generated.HeartbeatResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "heimdallr.HeartbeatService", "heartbeat"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.github.igorperikov.heimdallr.generated.HeartbeatRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.github.igorperikov.heimdallr.generated.HeartbeatResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static HeartbeatServiceStub newStub(io.grpc.Channel channel) {
    return new HeartbeatServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static HeartbeatServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new HeartbeatServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static HeartbeatServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new HeartbeatServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class HeartbeatServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void heartbeat(com.github.igorperikov.heimdallr.generated.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.github.igorperikov.heimdallr.generated.HeartbeatResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_HEARTBEAT, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_HEARTBEAT,
            asyncUnaryCall(
              new MethodHandlers<
                com.github.igorperikov.heimdallr.generated.HeartbeatRequest,
                com.github.igorperikov.heimdallr.generated.HeartbeatResponse>(
                  this, METHODID_HEARTBEAT)))
          .build();
    }
  }

  /**
   */
  public static final class HeartbeatServiceStub extends io.grpc.stub.AbstractStub<HeartbeatServiceStub> {
    private HeartbeatServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private HeartbeatServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeartbeatServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeartbeatServiceStub(channel, callOptions);
    }

    /**
     */
    public void heartbeat(com.github.igorperikov.heimdallr.generated.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.github.igorperikov.heimdallr.generated.HeartbeatResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_HEARTBEAT, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class HeartbeatServiceBlockingStub extends io.grpc.stub.AbstractStub<HeartbeatServiceBlockingStub> {
    private HeartbeatServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private HeartbeatServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeartbeatServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeartbeatServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.github.igorperikov.heimdallr.generated.HeartbeatResponse heartbeat(com.github.igorperikov.heimdallr.generated.HeartbeatRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_HEARTBEAT, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class HeartbeatServiceFutureStub extends io.grpc.stub.AbstractStub<HeartbeatServiceFutureStub> {
    private HeartbeatServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private HeartbeatServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeartbeatServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeartbeatServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.igorperikov.heimdallr.generated.HeartbeatResponse> heartbeat(
        com.github.igorperikov.heimdallr.generated.HeartbeatRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_HEARTBEAT, getCallOptions()), request);
    }
  }

  private static final int METHODID_HEARTBEAT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final HeartbeatServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(HeartbeatServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HEARTBEAT:
          serviceImpl.heartbeat((com.github.igorperikov.heimdallr.generated.HeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<com.github.igorperikov.heimdallr.generated.HeartbeatResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class HeartbeatServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.github.igorperikov.heimdallr.generated.HeimdallrOuterService.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (HeartbeatServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new HeartbeatServiceDescriptorSupplier())
              .addMethod(METHOD_HEARTBEAT)
              .build();
        }
      }
    }
    return result;
  }
}
