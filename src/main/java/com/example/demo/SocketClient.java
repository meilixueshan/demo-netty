package com.example.demo;

import java.net.Socket;
import java.nio.ByteBuffer;

public class SocketClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(NettyHostPort.HOST, NettyHostPort.PORT);

        final String str = "hello";
        byte[] bytes = str.getBytes();

        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + bytes.length);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);

        byte[] data = byteBuffer.array();

        for (int i = 0; i < 20; i++) {
            socket.getOutputStream().write(data);
        }
        socket.close();
    }
}
