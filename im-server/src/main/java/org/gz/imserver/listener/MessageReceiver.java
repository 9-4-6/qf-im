package org.gz.imserver.listener;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.gz.imcommon.constants.MqConstant;

import java.util.Objects;
@Slf4j
public class MessageReceiver  {
    private static Integer brokerId;
    public static void init(Integer brokerId) throws MQClientException {
        if (Objects.nonNull(MessageReceiver.brokerId)) {
            MessageReceiver.brokerId = brokerId;
        }
        startMessageReceiver();
    }
    private static void startMessageReceiver() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(MqConstant.IM_CHAT_SINGLE+brokerId);
        consumer.setNamesrvAddr("192.168.0.107:9876");
        consumer.setPullBatchSize(1);
        consumer.setConsumeMessageBatchMaxSize(1);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.setMaxReconsumeTimes(0);
        consumer.subscribe(MqConstant.IM_CHAT_SINGLE, String.valueOf(brokerId));
        consumer.registerMessageListener((MessageListenerConcurrently) (msgS, context) -> {
            for (MessageExt msg : msgS) {
                log.info("接受到消息:{}", JSONUtil.toJsonStr(msg));

            }
            return null;
        });

        consumer.start();

    }
}
