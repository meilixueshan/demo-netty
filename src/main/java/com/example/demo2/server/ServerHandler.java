package com.example.demo2.server;

import com.example.demo2.protocol.CustomMsg;
import com.example.demo2.protocol.ProtocolFlag;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

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

        //如果是登录消息，消息体就是userId。记录userId和channelId的映射关系
        if (flag == ProtocolFlag.FLAG_LOGIN) {
            ClientChannel.put(entityMessage.getBody(), ctx.channel().id());
        } else if (flag == ProtocolFlag.FLAG_LOGOUT) {
            ClientChannel.remove(entityMessage.getBody());
        } else {
            //转发消息
            String userId = ".....";    //userId应该从entityMessage.getBody()中转json，然后获取到userId
            Optional<Channel> optionalChannel = getChannel(userId);
            if (optionalChannel.isPresent()) {
                optionalChannel.get().writeAndFlush(entityMessage);
            }
        }

        ctx.channel().writeAndFlush(entityMessage);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();  //其实相当于一个connection
        channelGroup.writeAndFlush(" 【服务器】 -" + channel.remoteAddress() + " 加入\n");
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush(" 【服务器】 -" + channel.remoteAddress() + " 离开\n");

        //验证一下每次客户端断开连接，连接自动地从channelGroup中删除调。
        log.debug("channelGroup.size()={}", channelGroup.size());
        //当客户端和服务端断开连接的时候，下面一行代码netty会自动调用，所以不需要人为的去调用它
        //channelGroup.remove(channel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug(channel.remoteAddress() + " 上线了");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug(channel.remoteAddress() + " 下线了");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private Optional<Channel> getChannel(String userId) {
        Optional<ChannelId> optionalChannelId = ClientChannel.get(userId);
        if (optionalChannelId.isPresent()) {
            Channel channel = channelGroup.find(optionalChannelId.get());
            return Optional.ofNullable(channel);
        }
        return Optional.empty();
    }
}