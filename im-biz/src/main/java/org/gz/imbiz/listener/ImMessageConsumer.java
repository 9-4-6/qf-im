package org.gz.imbiz.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = "im_producer_group", topic = "im-chat")
@Slf4j
public class ImMessageConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String msg) {
        log.info("接受到消息:{}",msg);

    }
}
