package com.example.demo3.client;

import com.example.demo3.NettyHostPort;
import com.example.demo3.protocol.CustomMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CustomMsg entityMessage = (CustomMsg) msg;
        if (entityMessage == null) {
            throw new Exception("msg转CustomMsg为空");
        }

        System.out.println(String.format("ip:%s %s", ctx.channel().remoteAddress(), entityMessage));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("掉线了...");

        //使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                log.error("服务器连不上，开始重连操作...");

                NettyClient client = new NettyClient(NettyHostPort.HOST, NettyHostPort.PORT);
                client.start();
            }
        }, 1L, TimeUnit.SECONDS);

        super.channelInactive(ctx);
    }

    /**
     * 心跳检测
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            if (state == IdleState.READER_IDLE) {
                log.info("长期没收到服务器推送数据");
            } else if (state == IdleState.WRITER_IDLE) {
                log.info("长期未向服务器发送数据");

                ctx.writeAndFlush(buildHeartBeat());    //发送心跳包
            } else if (state == IdleState.ALL_IDLE) {
                log.info("长期没有数据接收或者发送");
            }
        }
    }

    private CustomMsg buildHeartBeat() {
        String msgBody = "";
        CustomMsg customMsg = new CustomMsg(
                (byte) 0xAB,
                (byte) 0xCD,
                msgBody.length(),
                msgBody);
        return customMsg;
    }
}