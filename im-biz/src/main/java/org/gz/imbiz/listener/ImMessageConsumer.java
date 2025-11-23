package org.gz.imbiz.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = "${spring.rocketmq.consumer.topics.imChat}",
        consumerGroup = "${spring.rocketmq.consumer.group}",
        messageModel = MessageModel.CLUSTERING
)
@Slf4j
public class ImMessageConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String msg) {
        log.info("接受到消息:{}",msg);

    }
}
