package com.example.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.util.StringUtils;

public class ThingFrameEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String s, ByteBuf byteBuf) throws Exception {
        try {
            if (!StringUtils.isEmpty(s)) {
                byte[] data = s.getBytes();
                byteBuf.writeInt(data.length);
                byteBuf.writeBytes(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}