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
        //读取消息头,整个消息的长度字段。可读字节小于4，说明消息长度还没满，直接返回
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        // 设置回滚点
        byteBuf.markReaderIndex();

        // 读取前四个字节(消息头)存储的消息长度，同时会使readerIndex向前移动4个指针
        int dataLength = byteBuf.readInt();

        // 读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
        if (dataLength <= 0) {
            ctx.close();
        }

        // 如果可读字节数小于消息长度，说明消息还不完整。
        if (byteBuf.readableBytes() < dataLength) {
            // 重置读指针，并返回
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        // 将ByteBuf中的数据读到字节数组中
        byteBuf.readBytes(data); // 与byteBuf.readBytes(data, 0, dataLength)等价

        //将字节数组反序列化为对象
        CustomMsg customMsg = SerializerUtils.deserialize(data, CustomMsg.class);

        out.add(customMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}