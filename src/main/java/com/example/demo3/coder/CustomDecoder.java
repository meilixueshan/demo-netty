package com.example.demo3.coder;

import com.example.demo3.protocol.CustomMsg;
import com.example.demo3.utils.SerializerUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class CustomDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        //读取消息头,整个消息的长度字段
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();//读取一个规定的int，即长度
        if (dataLength < 0) {
            ctx.close();
        }

        //读取字节数组,直到读取的字节数组长度等于dataLength
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);

        //将字节数组使用Hession反序列化为对象
        CustomMsg customMsg = SerializerUtils.deserialize(data, CustomMsg.class);

        out.add(customMsg);
    }
}
