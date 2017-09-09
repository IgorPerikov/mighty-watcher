package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.init.ClientBootstrapHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@AllArgsConstructor
@Slf4j
public class InterNodeMessageSender {
    private final HeimdallrNode sender;
    private final EventLoopGroup eventLoopGroup;
    private final InetSocketAddress address;

    public void send() throws InterruptedException {
        Bootstrap bootstrap = ClientBootstrapHelper.build(eventLoopGroup, address, sender);
        ChannelFuture connectFuture = bootstrap.connect();
        log.info("Sending request to node {}", address);
        ClusterStateTO build = ClusterStateTO.newBuilder()
                .putNodes(sender.getLabel().toString(), sender.getNodeDefinition())
                .build();
        connectFuture.sync().channel().writeAndFlush(build).sync();
    }
}
