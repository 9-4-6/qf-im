package org.gz.imbiz.listener;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.gz.imcommon.constants.RedisConstant;
import org.gz.qfinfra.rocketmq.producer.RocketmqProducer;
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
        maxReconsumeTimes = 3,
        messageModel = MessageModel.CLUSTERING)
@Slf4j
public class ImMessageConsumer implements RocketMQListener<MessageExt> {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private  RocketmqProducer rocketmqProducer;

    @Override
    public void onMessage(MessageExt msg) {
        String msgStr = new String(msg.getBody(), StandardCharsets.UTF_8);
        log.info("接受到消息:{}",msgStr);
        JSONObject entries = JSONUtil.parseObj(msgStr);
        Long userId = entries.getLong("userId");
        RMap<Long, String> userInstanceHash = redissonClient.getMap(RedisConstant.USER_INSTANCE_HASH_KEY);
        String instanceId = userInstanceHash.get(userId);

        //发送消息
        rocketmqProducer.sendSyncMessage(
                "im-chat-single",
                instanceId,
                null,
                msgStr,
                (msgContent, topic, tags, keys, ex, sendResult) -> {
                    // 异常日志
                    log.error("发送消息异常 | topic={}, tags={}, keys={},msg={}",
                            msgContent, topic, tags, keys, ex);

                }
        );


    }
}
