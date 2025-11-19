package org.gz.imserver.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.gz.imserver.proto.Message;
import org.gz.imserver.utils.ByteBufToMessageUtils;

import java.util.List;

/**
 * @author 17853
 * 自定义协议消息解码器
 */
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if(in.readableBytes() < 28){
            return;
        }
        //请求头（指令 版本 clientType 消息解析类型 appId imei长度 消息长度）
        //imei号（设备唯一标识）
        //请求体
        Message message = ByteBufToMessageUtils.transition(in);
        if(message == null){
            return;
        }

        out.add(message);
    }
}
