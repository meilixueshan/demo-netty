package com.example.demo2.utils;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 废弃的原因：hessian的性能低于MessagePack，而且序列化出的大小比MessagePack大
 */
@Deprecated
public class HessianSerializer {
    public static <T> byte[] serialize(T obj) {
        byte[] bytes = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        HessianOutput hessianOutput = new HessianOutput(bos);
        //hessianOutput.setSerializerFactory(serializerFactory);

        try {
            // 注意，obj 必须实现Serializable接口
            hessianOutput.writeObject(obj);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static <T> T deserialize(byte[] data) {
        if (data == null) {
            return null;
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        HessianInput hessianInput = new HessianInput(bis);
        Object object = null;
        try {
            object = hessianInput.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (T) object;
    }
}