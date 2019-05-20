package com.example.demo2.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息协议数据包
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomMsg implements Serializable {
    //类型  系统编号 1 表示A系统，2 表示B系统
    private byte type;

    //信息标志  -128表示心跳包  -127表示超时包  -126登录包   -125退出包  1...127业务信息包
    private byte flag;

    //主题信息的长度
    private int length;

    //主题信息
    private String body;
}
