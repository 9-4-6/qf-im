package org.gz.imserver.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.gz.imserver.proto.Message;


/**
 * @author guozhong
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

    }
}
