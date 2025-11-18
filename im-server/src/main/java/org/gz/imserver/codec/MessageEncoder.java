package org.gz.imserver.codec;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.gz.imserver.proto.MessagePack;

import java.nio.charset.StandardCharsets;


/**
 * @author 17853
 */
public class MessageEncoder extends MessageToByteEncoder<MessagePack<Object>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack msg, ByteBuf out) {
        String str = JSONUtil.toJsonStr(msg.getData());
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        out.writeInt(msg.getCommand());
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

}
