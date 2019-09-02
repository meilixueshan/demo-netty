package com.example.demo2.coder;

import com.example.demo2.protocol.CustomMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CustomEncoder extends MessageToByteEncoder<CustomMsg> {
    @Override
    protected void encode(ChannelHandlerContext ctx, CustomMsg msg, ByteBuf out) throws Exception {

        if (null == msg) {
            throw new Exception("msg is null");
        }

        //NSG:|1|1|4|BODY|
        out.writeByte(msg.getType());      //系统编号
        out.writeByte(msg.getFlag());      //信息标志
        out.writeInt(msg.getLength());   //消息长度
        out.writeBytes(msg.getBytes());         //消息正文
    }
}