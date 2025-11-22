package org.gz.imserver.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import org.gz.imserver.proto.Message;
import org.gz.imserver.proto.MessageHeader;

/**
 * @author: guozhong
 * @description: 将ByteBuf转化为Message实体，根据私有协议转换
 *               私有协议规则，
 *               4位表示Command表示消息的开始，
 *               4位表示version
 *               4位表示clientType
 *               4位表示messageType
 *               4位表示appId
 *               4位表示deviceId长度
 *               deviceId
 *               4位表示数据长度
 *               data
 */
public class ByteBufToMessageUtils {

    public static Message transition(ByteBuf in){

        // command
        int command = in.readInt();
        // version
        int version = in.readInt();

        // clientType 客户端类型(Android/iOS/PC/WEB)
        int clientType = in.readInt();

        // messageType 消息类型
        int messageType = in.readInt();

        // appId 应用id
        int appId = in.readInt();

        // deviceIdLength 设备唯一标识长度
        int deviceIdLength = in.readInt();

        // bodyLen 消息体长度
        int bodyLen = in.readInt();
        //为了防止半包问题
        int readableBytes = in.readableBytes();
        if(readableBytes < bodyLen + deviceIdLength){
            //长度不够，等等下一次读取
            in.resetReaderIndex();
            return null;
        }

        byte [] deviceIdData = new byte[deviceIdLength];
        in.readBytes(deviceIdData);
        String deviceId = new String(deviceIdData);

        byte [] bodyData = new byte[bodyLen];
        in.readBytes(bodyData);


        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setAppId(appId);
        messageHeader.setClientType(clientType);
        messageHeader.setCommand(command);
        messageHeader.setLength(bodyLen);
        messageHeader.setVersion(version);
        messageHeader.setMessageType(messageType);
        messageHeader.setDeviceIdLength(deviceIdLength);
        messageHeader.setLength(bodyLen);
        Message message = new Message();
        //请求头
        message.setMessageHeader(messageHeader);
        //请求唯一标识
        message.setDeviceId(deviceId);
        //请求体
        if(messageType == 0){
            String body = new String(bodyData);
            JSONObject parse =JSONUtil.parseObj(body);
            message.setMessageBody(parse);
        }
        //标记读取位置
        in.markReaderIndex();
        return message;
    }

}
