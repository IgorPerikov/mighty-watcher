package com.github.igorperikov.heimdallr.init;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ServerBootstrapHelper {
    public static ServerBootstrap build(
            EventLoopGroup parentEventLoopGroup,
            EventLoopGroup childEventLoopGroup,
            int port,
            HeimdallrNode node
    ) {
        return new ServerBootstrap()
                .group(parentEventLoopGroup, childEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new NodeServerChannelInitializer(node));
    }
}
