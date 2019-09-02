package com.example.demo2;

public class NettyHostPort {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8889;


    /**
     *    maxFrameLength：解码的帧的最大长度
     *    lengthFieldOffset ：长度属性的起始位（偏移位），包中存放有整个大数据包长度的字节，这段字节的其实位置
     *    lengthFieldLength：长度属性的长度，即存放整个大数据包长度的字节所占的长度
     *    lengthAdjustmen：长度调节值，在总长被定义为包含包头长度时，修正信息长度。
     *    initialBytesToStrip：跳过的字节数，根据需要我们跳过lengthFieldLength个字节，以便接收端直接接受到不含“长度属性”的内容
     *    failFast ：为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异常
     */
    public static final int MAX_FRAME_LENGTH = 1024 * 1024 * 10;
    public static final int LENGTH_FIELD_OFFSET = 2;
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int LENGTH_ADJUSTMENT = 0;
    public static final int INITIAL_BYTES_TO_STRIP = 0;
}