package org.gz.imserver.Listener;

import org.gz.qfinfra.rocketmq.consumer.adapter.QfRocketMqConsumerAdapter;
import org.gz.qfinfra.rocketmq.consumer.annotation.QfRocketMqMessageListener;
import org.springframework.stereotype.Component;

@Component
@QfRocketMqMessageListener(
        topic = "im_topic",
        consumerGroup = "${spring.cloud.rocketmq.consumer.group}",
        // 动态指定 Tag 为当前实例 ID（Spring EL 表达式）
        selectorExpression = "#{T(org.gz.imserver.manager.NacosInstanceComponent).getInstanceId()}"
)
public class ImMessageConsumer extends QfRocketMqConsumerAdapter<String> {

    @Override
    public void onMessage(String message) {

    }
}
