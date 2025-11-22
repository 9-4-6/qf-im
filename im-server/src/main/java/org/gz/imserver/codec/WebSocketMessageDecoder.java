package org.gz.imserver.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.gz.imserver.proto.Message;
import org.gz.imserver.utils.ByteBufToMessageUtils;

import java.util.List;

/**
 * @author 17853
 * 解码
 */
public class WebSocketMessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out)  {

        ByteBuf content = msg.content();
        int readableBytes = content.readableBytes();
        if (readableBytes < 28) {
            return;
        }
        Message message = ByteBufToMessageUtils.transition(content);
        if(message == null){
            return;
        }
        out.add(message);
    }
}
