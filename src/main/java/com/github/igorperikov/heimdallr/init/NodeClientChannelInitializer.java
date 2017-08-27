package com.github.igorperikov.heimdallr.init;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.generated.ClusterState;
import com.github.igorperikov.heimdallr.handler.ClientInboundChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NodeClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final HeimdallrNode node;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new ProtobufVarint32FrameDecoder(),
                new ProtobufDecoder(ClusterState.getDefaultInstance()),
                new ProtobufVarint32LengthFieldPrepender(),
                new ProtobufEncoder(),
                new ClientInboundChannelHandler(node)
        );
    }
}
