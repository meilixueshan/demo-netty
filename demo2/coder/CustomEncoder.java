package com.example.demo2.coder;

import com.example.demo2.protocol.CustomMsg;
import com.example.demo2.utils.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CustomEncoder extends MessageToByteEncoder<CustomMsg> {

    public String Encoding = "utf-8";

    @Override
    protected void encode(ChannelHandlerContext ctx, CustomMsg msg, ByteBuf out) throws Exception {

        if (null == msg) {
            throw new Exception("msg is null");
        }

        /*String body = msg.getBody();

        byte[] bodyBytes = body.getBytes(Charset.forName(Encoding));

        //NSG:|1|1|4|BODY|
        out.writeByte(msg.getType());      //系统编号
        out.writeByte(msg.getFlag());      //信息标志
        out.writeInt(bodyBytes.length);   //消息长度
        out.writeBytes(bodyBytes);         //消息正文*/

        byte[] data = HessianSerializer.serialize(msg);
        //先写入消息的长度作为消息头
        out.writeInt(data.length);
        //最后写入消息体字节数组
        out.writeBytes(data);
    }
}