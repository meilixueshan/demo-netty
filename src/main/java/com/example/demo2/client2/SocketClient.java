package com.example.demo2.client2;

import com.example.demo.NettyHostPort;

import java.net.Socket;
import java.nio.ByteBuffer;

public class SocketClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(NettyHostPort.HOST, NettyHostPort.PORT);

        final byte type = 11;
        final byte flag = 12;
        final String str = "hello";
        byte[] bytes = str.getBytes();

        //type的长度 + flag的长度 + int的长度（字符串的长度是int类型） + 字符串换算成字节后的长度
        ByteBuffer byteBuffer = ByteBuffer.allocate(1 + 1 + 4 + bytes.length);
        byteBuffer.put(type);
        byteBuffer.put(flag);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);

        byte[] data = byteBuffer.array();

        for (int i = 0; i < 20; i++) {
            socket.getOutputStream().write(data);
        }
        socket.close();
    }
}
