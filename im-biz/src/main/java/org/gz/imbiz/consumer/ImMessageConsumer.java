package org.gz.imbiz.consumer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.gz.imcommon.constants.RedisConstant;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author 17853
 */
@Component
@RocketMQMessageListener(consumerGroup = "im-chat",
        topic = "im-chat",
        maxReconsumeTimes = 0,
        messageModel = MessageModel.CLUSTERING)
@Slf4j
public class ImMessageConsumer implements RocketMQListener<MessageExt> {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(MessageExt msg) {
        String msgStr = new String(msg.getBody(), StandardCharsets.UTF_8);
        log.info("接受到消息:{}",msgStr);
        JSONObject entries = JSONUtil.parseObj(msgStr);
        Long userId = entries.getLong("userId");
        RMap<Long, String> userInstanceHash = redissonClient.getMap(RedisConstant.USER_INSTANCE_HASH_KEY);
        String instanceId = userInstanceHash.get(userId);
        //发送消息
        rocketMqTemplate.asyncSend("im-chat-single", instanceId, new SendCallback() {
            public void onSuccess(SendResult r) {}
            public void onException(Throwable e) {
                log.error("发送失败: {}", "im-chat-single", e);
            }
        });
    }
}
