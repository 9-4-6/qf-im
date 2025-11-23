package org.gz.imserver.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        consumerGroup = "im_producer_group",
        topic = "im-chat",
        selectorExpression = "#{T(org.gz.imserver.manager.NacosInstanceComponent).getInstanceId()}",
        messageModel = MessageModel.CLUSTERING
)
@Slf4j
public class ImNettyMsgConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
       log.info("接受到消息:{}",message);
    }
}
