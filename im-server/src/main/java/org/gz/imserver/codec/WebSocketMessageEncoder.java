package org.gz.imserver.codec;



import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.gz.imserver.proto.MessagePack;


import java.util.List;
/**
 * @author 17853
 */
public class WebSocketMessageEncoder extends MessageToMessageEncoder<MessagePack> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack msg, List<Object> out)  {
            String str = JSONUtil.toJsonStr(msg.getData());
            ByteBuf byteBuf = Unpooled.directBuffer(8+str.length());
            byte[] bytes = str.getBytes();
            byteBuf.writeInt(msg.getCommand());
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            out.add(new BinaryWebSocketFrame(byteBuf));
    }
}