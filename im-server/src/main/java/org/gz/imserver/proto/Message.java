package org.gz.imserver.proto;

import lombok.Data;

/**
 * 消息
 * @author 17853
 */
@Data
public class Message {

    private MessageHeader messageHeader;

    private String deviceId;

    private Object messageBody;

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                "deviceId=" + deviceId +
                ", messageBody=" + messageBody +
                '}';
    }
}
