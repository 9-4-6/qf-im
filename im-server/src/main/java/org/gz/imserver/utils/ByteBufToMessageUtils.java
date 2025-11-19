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
 *               4位表示imei长度
 *               imei
 *               4位表示数据长度
 *               data
 */
public class ByteBufToMessageUtils {

    public static Message transition(ByteBuf in){

        // command
        int command = in.readInt();

        // version
        int version = in.readInt();

        // clientType 客户端类型
        int clientType = in.readInt();

        // messageType 消息类型
        int messageType = in.readInt();

        // appId 应用id
        int appId = in.readInt();

        // imeiLength 设备唯一值长度
        int imeiLength = in.readInt();

        // bodyLen 消息体长度
        int bodyLen = in.readInt();

        if(in.readableBytes() < bodyLen + imeiLength){
            in.resetReaderIndex();
            return null;
        }

        byte [] imeiData = new byte[imeiLength];
        in.readBytes(imeiData);
        String imei = new String(imeiData);

        byte [] bodyData = new byte[bodyLen];
        in.readBytes(bodyData);


        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setAppId(appId);
        messageHeader.setClientType(clientType);
        messageHeader.setCommand(command);
        messageHeader.setLength(bodyLen);
        messageHeader.setVersion(version);
        messageHeader.setMessageType(messageType);
        messageHeader.setImei(imei);

        Message message = new Message();
        message.setMessageHeader(messageHeader);

        if(messageType == 0x0){
            String body = new String(bodyData);
            JSONObject parse =JSONUtil.parseObj(body);
            message.setMessagePack(parse);
        }

        in.markReaderIndex();
        return message;
    }

}
