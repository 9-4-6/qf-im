package org.gz.imserver.listener;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.gz.imcommon.constants.MqConstant;
import org.gz.imcommon.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
/**
 * @author 17853
 */
@Slf4j
@Component
public class MessageReceiver  {
    @Value("${rocketmq.nameServer}")
    private String nameServer;
    @Value("${netty.server.brokerId}")
    private  Integer brokerId;
    public void init() throws MQClientException {
        Assert.isTrue(Objects.nonNull(brokerId),()->new BizException("IM消费者启动失败,brokerId不存在"));
        startMessageReceiver();
    }
    private  void startMessageReceiver() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(MqConstant.IM_CHAT_SINGLE+brokerId);
        consumer.setNamesrvAddr(nameServer);
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
