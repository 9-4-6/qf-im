package org.gz.imserver.proto;

import lombok.Data;

/**
 * @author guozhong
 * @date 2025/11/18
 * 消息头
 */
@Data
public class MessageHeader {
    //4字节
    private Integer command;
    //4字节 版本号
    private Integer version;
    //4字节 端类型
    private Integer clientType;
    // 应用ID   4字节 appId
    private Integer appId;
    //数据解析类型 0 代表json 用于后面扩展
    private Integer messageType = 0;
    //4字节 设备唯一标识长度
    private Integer deviceIdLength;

    //4字节 包体长度
    private int length;
}
