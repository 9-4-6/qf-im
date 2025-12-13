package org.gz.imserver.listener;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.gz.imcommon.enums.SystemCommandEnum;
import org.gz.imserver.netty.SessionSocketHolder;
import org.gz.imserver.proto.MessageResponse;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author 17853
 */
@Component
@RocketMQMessageListener(
        consumerGroup = "${spring.cloud.nacos.discovery.ip}:${server.port}",
        topic = "im-chat-single",
        selectorType = SelectorType.TAG,
        maxReconsumeTimes = 0,
        selectorExpression = "${spring.cloud.nacos.discovery.ip}:${server.port}",
        messageModel = MessageModel.CLUSTERING
)
@Slf4j
public class ImNettyMsgConsumer implements RocketMQListener<MessageExt> {
    @Override
    public void onMessage(MessageExt message) {
        log.info("接受到消息:{}",message);
        String msgStr = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("接受到消息:{}",msgStr);
        JSONObject entries = JSONUtil.parseObj(msgStr);
        Long userId = entries.getLong("userId");
        Channel channel = SessionSocketHolder.get(userId);
        if (Objects.nonNull(channel)){
            MessageResponse<String> msgR = new MessageResponse<>();
            msgR.setCommand(SystemCommandEnum.SINGLE_CHAT.getCommand());
            msgR.setData("我收到你消息了");
            channel.writeAndFlush(msgR);
        }
    }
}
