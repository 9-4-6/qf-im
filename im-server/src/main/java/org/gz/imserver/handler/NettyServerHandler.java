package org.gz.imserver.handler;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.gz.imcommon.enums.MessageCommandEnum;
import org.gz.imcommon.enums.SystemCommandEnum;
import org.gz.imserver.proto.Message;
import org.gz.imserver.proto.MessagePack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author guozhong
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
    //todo 暂时存本地，后面修改
    public static final Map<Long, Channel> userChannelMap = new ConcurrentHashMap<>();

    /**
     * 核心方法：处理客户端发送的消息（已解码为Message对象）
     * 当ChannelPipeline中前面的解码器（如自定义的ByteBufToMessageDecoder）将ByteBuf解析为Message对象后，会触发此方法
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        String clientAddress = ctx.channel().remoteAddress().toString();
        log.info("收到客户端[{}]的消息：{}", clientAddress, msg);

        int command = msg.getMessageHeader().getCommand();
        log.info("消息命令为：{}，开始处理业务逻辑", command);
        JSONObject jsonObject = JSONUtil.parseObj(msg.getMessagePack());
        if(command == SystemCommandEnum.LOGIN.getCommand()){
            //登录
            Long userId = jsonObject.getLong("userId");
            MessagePack<Long> loginSuccess = new MessagePack<>();
            loginSuccess.setCommand(SystemCommandEnum.LOGIN_ACK.getCommand());
            loginSuccess.setData(userId);
            ctx.channel().writeAndFlush(loginSuccess);
            userChannelMap.put(userId,ctx.channel());
        }else if(command == MessageCommandEnum.MSG_P2P.getCommand()){
            //单聊
            //接收人
            Long toId = jsonObject.getLong("toId");
            //消息内容
            Long centent = jsonObject.getLong("content");
            Channel channel = userChannelMap.get(toId);
            channel.writeAndFlush(centent);
        }

    }

    /**
     * 处理用户事件（非I/O事件），常见场景：心跳检测（IdleStateEvent）、连接状态变更等
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 判断事件类型是否为空闲事件（由IdleStateHandler触发）
        if (evt instanceof IdleStateEvent idleEvent) {
            String clientAddress = ctx.channel().remoteAddress().toString();

            // 根据空闲类型打印日志（读空闲/写空闲/读写空闲）
            switch (idleEvent.state()) {
                case READER_IDLE:
                    log.warn("客户端[{}]触发读空闲事件（长时间未发送消息）", clientAddress);
                    // 业务处理：例如关闭连接、发送心跳检测请求等
                    ctx.close(); // 示例：关闭空闲连接
                    break;
                case WRITER_IDLE:
                    log.info("客户端[{}]触发写空闲事件（长时间未接收消息）", clientAddress);
                    break;
                case ALL_IDLE:
                    log.info("客户端[{}]触发读写空闲事件", clientAddress);
                    break;
            }
        } else {
            // 非空闲事件，交给父类处理（默认不做任何操作）
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 处理channel中的异常（如网络中断、解码失败、业务逻辑异常等）
     * 若不重写此方法，默认会关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String clientAddress = ctx.channel().remoteAddress().toString();
        // 打印异常信息（包含客户端地址，便于定位问题）
        log.error("客户端[{}]发生异常，原因：", clientAddress, cause);

        // 异常处理策略：根据异常类型决定是否关闭连接
        // 示例：网络异常直接关闭，业务异常可返回错误响应后关闭
        ctx.close(); // 通常建议关闭异常连接，避免资源泄漏
    }

    // 补充：常用的其他生命周期方法（可选重写）
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String clientAddress = ctx.channel().remoteAddress().toString();
        log.info("客户端[{}]连接成功", clientAddress);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientAddress = ctx.channel().remoteAddress().toString();
        log.info("客户端[{}]断开连接", clientAddress);
        super.channelInactive(ctx);
    }
}
