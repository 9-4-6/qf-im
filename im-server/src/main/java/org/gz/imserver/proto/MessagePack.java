package org.gz.imserver.proto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 17853
 * */
@Data
public class MessagePack<T> implements Serializable {

    private String userId;

    private Integer appId;

    /**
     * 接收方
     */
    private String toId;

    /**
     * 客户端标识
     */
    private int clientType;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 设备唯一标识
     */
    private String deviceId;

    private Integer command;

    /**
     * 业务数据对象，如果是聊天消息则不需要解析直接透传
     */
    private T data;

}
