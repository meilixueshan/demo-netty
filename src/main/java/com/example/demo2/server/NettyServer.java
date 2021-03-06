package com.example.demo2.server;

import com.example.demo2.NettyHostPort;
import com.example.demo2.coder.CustomDecoder;
import com.example.demo2.coder.CustomEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 参看：https://github.com/smartbooks/netty-example
 */
@Slf4j
public class NettyServer {
    public void run(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        //可以直接用new LengthFieldBasedFrameDecoder(..)，不需要自己定义decoder
                        p.addLast(new CustomDecoder(
                                NettyHostPort.MAX_FRAME_LENGTH,
                                NettyHostPort.LENGTH_FIELD_OFFSET,
                                NettyHostPort.LENGTH_FIELD_LENGTH,
                                NettyHostPort.LENGTH_ADJUSTMENT,
                                NettyHostPort.INITIAL_BYTES_TO_STRIP,
                                false));
                        p.addLast(new CustomEncoder());
                        p.addLast(new ServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            if (future.isSuccess()) {
                log.info("服务端启动成功！");
            } else {
                log.error("服务端启动失败！");
                future.cause().printStackTrace();
            }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务器启动失败！原因：{}", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyServer().run(NettyHostPort.PORT);    //启动server服务
    }
}