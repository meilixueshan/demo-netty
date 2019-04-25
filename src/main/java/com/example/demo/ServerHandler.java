package com.example.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private final String DATA_HEAD = "ffeeaa55";
    private final String DATA_END = "a10f3cff";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;

        if (message.startsWith(DATA_HEAD) && message.endsWith(DATA_END)) {
            log.info("服务端收到合格的数据：{}", msg);
        } else {
            log.warn("服务端收到无效的数据：{}", msg);
            return;
        }

        ctx.writeAndFlush("hi");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}