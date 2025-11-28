package org.gz.imbiz.manager;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.gz.qfinfra.rocketmq.consumer.DeadLetterMessageHandler;
import org.springframework.stereotype.Component;

/**
 * @author guozhong
 * @date 2025/11/28
 * @description 私信队列消费者（测试）
 */
@Component
@Slf4j
public class ImDeadLetterHandler implements DeadLetterMessageHandler {
    @Override
    public boolean supports(MessageExt message) {
        String consumerGroup = extractConsumerGroup(message.getTopic());
        return consumerGroup.contains("im");
    }

    @Override
    public boolean handleMessage(MessageExt message) {
        try {
            log.error("🚨 im Dead Letter - MsgId: {}, Group: {}",
                    message.getMsgId(), extractConsumerGroup(message.getTopic()));

            //保存失败消息
            return true;
        } catch (Exception e) {
            log.error("Handle im dead letter failed: {}", message.getMsgId(), e);
            return false;
        }
    }


    private String extractConsumerGroup(String dlqTopic) {
        return dlqTopic.replace("%DLQ%", "");
    }
}
