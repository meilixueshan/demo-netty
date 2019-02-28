package com.example.demo;

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
                p.addLast(new ThingFrameEncoder());
                p.addLast(new ThingFrameDecoder());
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

                                NettyClient client = new NettyClient(NettyHostPort.HOST, NettyHostPort.PORT);
                                client.start();
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
        String str = "ffeeaa5507e30001000b00100028003a19801785000000000000000005200000000000000000000000000000000000000000000000000000005000001770694d53486561640001000400d10086036e0000000000000000000000000000006368300000000000000000000000000000feffffffffffffdc05000000000000fca9f1d24d62603f00000000000000000000000000000000000000000000343f0000000000000000000000000000f03f670000000000000000000000000000000000023c0000000000e880427f959e3c00001301190b2a1da1000000000000000000000000000000000000000000000088f4910660d822000000008068a97f03e04fbf0610d3220001000000a76fcb00000000000000204100000000000000000103000080d32200c03cde61e84fbf0600000000000000004a0000001800000000000000000000004a000000180000006368310000000000000000000000000000feffffffffffffdc05000000000000fca9f1d24d62603f00000000000000000000000000000000000000000000343f0000000000000000000000000000f03f670000000000000000000000000000000040493d0000000000b80842ba78673e00001301190b2a1da1000000000000000000000000000000000000000000000088f4910660d822000000008068a97f03e04fbf0610d3220001000000a76fcb00000000000000204100000000000000000103000080d32200c03cde61e84fbf0600000000000000004a0000001800000000000000000000004a000000180000003055000000000000000000000000000000000000000000000000000000000000fca9f1d24d62603f00000000000000000000000000000000000000000000343f0000000000000000000000000000f03f670000000000000000000000000000000096aa3f0000000000889041e098394100001301190b2a1da1000000000000000000000000000000000000000000000088f4910660d822000000008068a97f03e04fbf0610d3220001000000a76fcb00000000000000204100000000000000000103000080d32200c03cde61e84fbf0600000000000000004a0000001800000000000000000000004a000000180000006368330000000000000000000000000000feffffffffffffdc05000000000000fca9f1d24d62603f00000000000000000000000000000000000000000000343f0000000000000000000000000000f03f000000000000000000000000000000000000000000000000000000000000000000001301190b2a1da1000000000000000000000000000000000000000000000088f4910660d822000000008068a97f03e04fbf0610d3220001000000a76fcb00000000000000204100000000000000000103000080d32200c03cde61e84fbf0600000000000000004a0000001800000000000000000000004a000000180000003cff7dff3d0ca10f3aff7eff420ca10f36ff82ff410ca10f37ff82ff4f0ca10f3bff81ff4a0ca10f3bff81ff460ca10f3cff80ff4b0ca10f40ff83ff490ca10f3dff7dff490ca10f3fff7bff470ca10f39ff7cff440ca10f36ff7eff4b0ca10f3aff83ff500ca10f3dff7cff490ca10f42ff7eff470ca10f3dff7eff490ca10f3aff81ff460ca10f39ff82ff440ca10f3aff7fff440ca10f3dff82ff430ca10f3bff83ff4a0ca10f3bff82ff490ca10f43ff83ff460ca10f3cff7eff470ca10f3bff7dff410ca10f36ff7fff410ca10f37ff83ff490ca10f37ff86ff490ca10f3bff84ff4b0ca10f43ff83ff480ca10f41ff7dff450ca10f3eff7dff490ca10f3cff7dff3f0ca10f36ff81ff420ca10f33ff80ff460ca10f36ff7dff470ca10f35ff78ff4b0ca10f3aff80ff4d0ca10f3cff81ff4c0ca10f3dff7dff3f0ca10f35ff7bff3f0ca10f37ff82ff490ca10f39ff80ff480ca10f37ff82ff490ca10f34ff7fff4b0ca10f37ff80ff4d0ca10f3dff82ff4d0ca10f3fff7eff4c0ca10f3cff7cff420ca10f39ff79ff400ca10f3fff7eff450ca10f3bff80ff4a0ca10f39ff85ff4b0ca10f3cff81ff480ca10f41ff7dff4a0ca10f40ff7fff420ca10f3aff83ff3f0ca10f39ff83ff460ca10f35ff80ff440ca10f38ff7fff440ca10f38ff82ff480ca10f3eff88ff4d0ca10f3eff82ff470ca10f40ff7fff440ca10f3fff7eff3c0ca10f3eff7fff410ca10f38ff7fff440ca10f34ff7eff4c0ca10f35ff7fff500ca10f3cff83ff4c0ca10f3bff83ff490ca10f3cff81ff420ca10f3bff7eff420ca10f38ff82ff400ca10f36ff7fff4d0ca10f38ff7dff4c0ca10f37ff7fff480ca10f3aff80ff4f0ca10f3cff84ff4e0ca10f42ff85ff520ca10f41ff82ff500ca10f3eff82ff4b0ca10f3bff81ff470ca10f3dff84ff490ca10f39ff81ff4c0ca10f3bff82ff4c0ca10f39ff83ff450ca10f3cff84ff440ca10f39ff7fff410ca10f41ff7cff460ca10f3eff7eff490ca10f3dff7cff460ca10f40ff80ff420ca10f39ff83ff3d0ca10f38ff85ff400ca10f3dff83ff400ca10f3dff80ff400ca10f3bff80ff430ca10f40ff7bff430ca10f3cff80ff440ca10f39ff83ff440ca10f37ff83ff4e0ca10f37ff83ff4d0ca10f3dff81ff480ca10f3fff7cff480ca10f3cff7dff460ca10f3cff86ff4b0ca10f3dff82ff4a0ca10f3aff7dff500ca10f35ff7dff4e0ca10f3aff7cff500ca10f3dff85ff4c0ca10f3fff81ff4a0ca10f44ff82ff430ca10f3fff7fff460ca10f3dff7cff480ca10f44ff7dff4a0ca10f3dff7fff450ca10f3bff7bff460ca10f42ff7dff430ca10f40ff7cff410ca10f3cff";
        while (true) {
            for (int i = 0; i < 10; i++) {
                TimeUnit.SECONDS.sleep(3);
                channel.writeAndFlush(str);

                TimeUnit.SECONDS.sleep(1);
                channel.writeAndFlush("您好！欢迎光临成都！");
            }
            TimeUnit.MINUTES.sleep(10);
        }
    }
}