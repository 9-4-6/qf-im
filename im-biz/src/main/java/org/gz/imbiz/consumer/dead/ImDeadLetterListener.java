package org.gz.imbiz.consumer.dead;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.gz.qfinfra.exception.BizException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ImDeadLetterListener {
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("dlq-global-processor");
        consumer.setNamesrvAddr("192.168.0.107:9876");
        consumer.setConsumeThreadMin(5);
        consumer.setConsumeThreadMax(10);
        consumer.setPullBatchSize(10);
        consumer.setPullInterval(1000);
        String deadLetterTopic = "%DLQ%" + "im_consumer_group";
        consumer.subscribe(deadLetterTopic, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                try {
                    // 你的业务处理逻辑
                    log.info("收到死信消息: {}", new String(msg.getBody()));
                    // 业务处理逻辑
                    handleDeadLetter(msg);
                } catch (Exception e) {
                    // 处理失败，稍后重试
                    log.info("处理死信失败: {}", new String(msg.getBody()),e);
                }
            }
            // 处理成功，确认消费
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
        log.info("im_consumer_group 死信队列监听已启动");
    }

    private void handleDeadLetter(MessageExt message) {

    }
}
