package org.gz.imserver.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author 17853
 */
@Component
@RocketMQMessageListener(
        consumerGroup = "${spring.cloud.nacos.discovery.ip}:${server.port}",
        topic = "im-chat-single",
        selectorType = SelectorType.TAG,
        selectorExpression = "${spring.cloud.nacos.discovery.ip}:${server.port}",
        messageModel = MessageModel.CLUSTERING
)
@Slf4j
public class ImNettyMsgConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
       log.info("接受到消息:{}",message);
    }
}
