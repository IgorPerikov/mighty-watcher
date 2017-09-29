package com.github.igorperikov.heimdallr.generated;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.5.0)",
    comments = "Source: heimdallr/heimdallr-service.proto")
public final class HeimdallrServiceGrpc {

  private HeimdallrServiceGrpc() {}

  public static final String SERVICE_NAME = "heimdallr.HeimdallrService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.github.igorperikov.heimdallr.generated.ClusterStateTO,
      com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO> METHOD_GET_DIFF_WITH_OTHER_NODES_STATE =
      io.grpc.MethodDescriptor.<com.github.igorperikov.heimdallr.generated.ClusterStateTO, com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "heimdallr.HeimdallrService", "getDiffWithOtherNodesState"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.github.igorperikov.heimdallr.generated.ClusterStateTO.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static HeimdallrServiceStub newStub(io.grpc.Channel channel) {
    return new HeimdallrServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static HeimdallrServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new HeimdallrServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static HeimdallrServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new HeimdallrServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class HeimdallrServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getDiffWithOtherNodesState(com.github.igorperikov.heimdallr.generated.ClusterStateTO request,
        io.grpc.stub.StreamObserver<com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_DIFF_WITH_OTHER_NODES_STATE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_GET_DIFF_WITH_OTHER_NODES_STATE,
            asyncUnaryCall(
              new MethodHandlers<
                com.github.igorperikov.heimdallr.generated.ClusterStateTO,
                com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO>(
                  this, METHODID_GET_DIFF_WITH_OTHER_NODES_STATE)))
          .build();
    }
  }

  /**
   */
  public static final class HeimdallrServiceStub extends io.grpc.stub.AbstractStub<HeimdallrServiceStub> {
    private HeimdallrServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private HeimdallrServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeimdallrServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeimdallrServiceStub(channel, callOptions);
    }

    /**
     */
    public void getDiffWithOtherNodesState(com.github.igorperikov.heimdallr.generated.ClusterStateTO request,
        io.grpc.stub.StreamObserver<com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_DIFF_WITH_OTHER_NODES_STATE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class HeimdallrServiceBlockingStub extends io.grpc.stub.AbstractStub<HeimdallrServiceBlockingStub> {
    private HeimdallrServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private HeimdallrServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeimdallrServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeimdallrServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO getDiffWithOtherNodesState(com.github.igorperikov.heimdallr.generated.ClusterStateTO request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_DIFF_WITH_OTHER_NODES_STATE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class HeimdallrServiceFutureStub extends io.grpc.stub.AbstractStub<HeimdallrServiceFutureStub> {
    private HeimdallrServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private HeimdallrServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeimdallrServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeimdallrServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO> getDiffWithOtherNodesState(
        com.github.igorperikov.heimdallr.generated.ClusterStateTO request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_DIFF_WITH_OTHER_NODES_STATE, getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_DIFF_WITH_OTHER_NODES_STATE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final HeimdallrServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(HeimdallrServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_DIFF_WITH_OTHER_NODES_STATE:
          serviceImpl.getDiffWithOtherNodesState((com.github.igorperikov.heimdallr.generated.ClusterStateTO) request,
              (io.grpc.stub.StreamObserver<com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO>) responseObserver);
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

  private static final class HeimdallrServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.github.igorperikov.heimdallr.generated.HeimdallrOuterService.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (HeimdallrServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new HeimdallrServiceDescriptorSupplier())
              .addMethod(METHOD_GET_DIFF_WITH_OTHER_NODES_STATE)
              .build();
        }
      }
    }
    return result;
  }
}
