package com.example.demo2.server;

import com.example.demo2.protocol.CustomMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        byte type = byteBuf.readByte();
        byte flag = byteBuf.readByte();
        int length = byteBuf.readInt();

        int len = byteBuf.readableBytes();
        byte[] req = new byte[len];
        byteBuf.readBytes(req);
        String body = new String(req, "UTF-8");

        CustomMsg entityMessage = new CustomMsg(type, flag, length, body);

        System.out.println(String.format("ip:%s %s", ctx.channel().remoteAddress(), entityMessage));

        entityMessage.setBody(String.format("server:%s orogin:%s", System.currentTimeMillis(), entityMessage.getBody()));

        ctx.channel().writeAndFlush(entityMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}