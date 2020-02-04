package com.example.demo3.utils;

import org.msgpack.MessagePack;

import java.io.IOException;

public class SerializerUtils {
    public static <T> byte[] serialize(T obj) {
        byte[] bytes = null;

        MessagePack messagePack = new MessagePack();
        try {
            bytes = messagePack.write(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        if (data == null) {
            return null;
        }

        MessagePack messagePack = new MessagePack();
        try {
            return messagePack.read(data, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
