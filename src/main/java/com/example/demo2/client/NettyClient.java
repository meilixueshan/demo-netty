package com.example.demo2.client;

import com.example.demo2.NettyHostPort;
import com.example.demo2.coder.CustomDecoder;
import com.example.demo2.coder.CustomEncoder;
import com.example.demo2.protocol.CustomMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {
    private final String host;
    private final int port;
    private Channel channel;

    private static final int READ_IDEL_TIME = 4;    // 一段时间内没有数据接收
    private static final int WRITE_IDEL_TIME = 5;   // 一段时间内没有数据发送
    private static final int ALL_IDEL_TIME = 7;     // 一段时间内没有数据接收或者发送

    //连接服务端的端口号地址和端口号
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        final EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);  //使用NioSocketChannel来作为连接用的channel类
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new IdleStateHandler(READ_IDEL_TIME, WRITE_IDEL_TIME, ALL_IDEL_TIME, TimeUnit.SECONDS));
                //可以直接用new LengthFieldBasedFrameDecoder(..)，不需要自己定义decoder
                p.addLast(new CustomDecoder(
                        NettyHostPort.MAX_FRAME_LENGTH,
                        NettyHostPort.LENGTH_FIELD_OFFSET,
                        NettyHostPort.LENGTH_FIELD_LENGTH,
                        NettyHostPort.LENGTH_ADJUSTMENT,
                        NettyHostPort.INITIAL_BYTES_TO_STRIP,
                        false));
                p.addLast(new CustomEncoder());
                p.addLast(new ClientHandler());
            }
        });
        //发起异步连接请求，绑定连接端口和host信息
        final ChannelFuture future;
        try {
            future = bootstrap.connect(host, port).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        log.info("服务器连接成功...");
                    } else {
                        final EventLoop loop = channelFuture.channel().eventLoop();
                        loop.schedule(new Runnable() {
                            @Override
                            public void run() {
                                log.error("服务器连不上，开始重连操作...");

                                start();
                            }
                        }, 1L, TimeUnit.SECONDS);
                    }
                }


            });

            this.channel = future.channel();
        } catch (Exception e) {
            log.error("无法连接服务器:{}", e.getMessage());

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e1) {
                log.error("休眠失败！原因：{}", e1.getMessage());
            }

            start();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public static void main(String[] args) throws Exception {
        NettyClient client = new NettyClient(NettyHostPort.HOST, NettyHostPort.PORT);
        client.start();

        Channel channel = client.getChannel();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("中华人民共和国");
        }
        sb.append("正文结束了");

        while (true) {
            for (int i = 0; i < 10; i++) {
                String msgBody = String.format("client %d: %s", i, sb.toString());

                CustomMsg msgEntity = new CustomMsg(
                        (byte) 0xAB,
                        (byte) 0xCD,
                        msgBody.length(),
                        msgBody);

                TimeUnit.SECONDS.sleep(3);
                channel.writeAndFlush(msgEntity);
            }
            TimeUnit.MINUTES.sleep(10);
        }
    }
}