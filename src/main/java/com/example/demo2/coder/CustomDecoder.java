package com.example.demo2.coder;

import com.example.demo2.protocol.CustomMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class CustomDecoder extends LengthFieldBasedFrameDecoder {
    private static final int HEADER_SIZE = 6;

    /**
     * @param maxFrameLength      帧的最大长度
     * @param lengthFieldOffset   length字段偏移的地址
     * @param lengthFieldLength   length字段所占的字节长
     * @param lengthAdjustment    修改帧数据长度字段中定义的值，可以为负数 因为有时候我们习惯把头部记入长度,若为负数,则说明要推后多少个字段
     * @param initialBytesToStrip 解析时候跳过多少个长度
     * @param failFast            为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异
     */
    public CustomDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //在这里调用父类的方法,实现指得到想要的部分,我在这里全部都要,也可以只要body部分
        in = (ByteBuf) super.decode(ctx, in);

        if (in == null) {
            return null;
        }
        if (in.readableBytes() < HEADER_SIZE) {
            throw new Exception("字节数不足");
        }

        //读取type字段
        final byte type = in.readByte();
        //读取flag字段
        final byte flag = in.readByte();
        //读取length字段
        final int length = in.readInt();

        if (in.readableBytes() != length) {
            throw new Exception("标记的长度不符合实际长度");
        }
        //读取body
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);

        final String body = new String(bytes, "UTF-8");

        return new CustomMsg(type, flag, length, body);
    }
}
