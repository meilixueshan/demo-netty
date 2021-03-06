package com.example.demo2.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * 消息协议数据包
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomMsg implements Serializable {
    public final static String Encoding = "utf-8";

    //类型  系统编号 1 表示A系统，2 表示B系统
    private byte type;

    //信息标志  0xAA表示心跳包  0xAB表示超时包  0xAC登录包   0xAD退出包  其它为业务信息包
    private byte flag;

    //主题信息的长度
    private int length;

    //主题信息
    private String body;

    public int getLength() {
        if (body != null) {
            this.length = getBytes().length;
        }
        return this.length;
    }

    public byte[] getBytes() {
        return this.body.getBytes(Charset.forName(CustomMsg.Encoding));
    }
}
