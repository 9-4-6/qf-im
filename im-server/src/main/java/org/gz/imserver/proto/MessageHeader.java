package org.gz.imserver.proto;

import lombok.Data;

/**
 * @author guozhong
 * @date 2025/11/18
 * 消息头
 */
@Data
public class MessageHeader {
    //消息操作指令 十六进制 一个消息的开始通常以0x开头
    //4字节
    private Integer command;
    //4字节 版本号
    private Integer version;
    //4字节 端类型
    private Integer clientType;
    // 应用ID   4字节 appId
    private Integer appId;
    //数据解析类型 0x0
    private Integer messageType = 0x0;
    //4字节 设备唯一标识长度
    private Integer deviceIdLength;

    //4字节 包体长度
    private int length;

    //imei号
    private String imei;
}
