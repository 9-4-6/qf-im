package org.gz.imbiz.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author 17853
 */
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group}",
        topic = "${rocketmq.consumer.topic}",
        maxReconsumeTimes = 3,
        messageModel = MessageModel.CLUSTERING)
@Slf4j
public class ImMessageConsumer implements RocketMQListener<MessageExt> {
    @Override
    public void onMessage(MessageExt msg) {
        log.info("接受到消息:{}",new String(msg.getBody(), StandardCharsets.UTF_8));

        throw new RuntimeException("失败");

    }
}
