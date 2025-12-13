package org.gz.imcommon.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


@Configuration(proxyBeanMethods = false)
@Slf4j
@ConditionalOnClass(RocketMQTemplate.class)
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMqConfig {

    /**
     * 即时通讯专用Template
     */
    @Bean("rocketMqImTemplate")
    public RocketMQTemplate rocketMqImTemplate(RocketMQProperties rocketMQProperties) {
        log.info("Initializing IM no-retry RocketMQ producer...");
        // 使用特定的Producer Group
        DefaultMQProducer producer = new DefaultMQProducer("IM_NO_RETRY_GROUP");
        // 复用starter配置的name-server（也可以继续用@Value，二选一）
        producer.setNamesrvAddr(rocketMQProperties.getNameServer());
        // 核心配置：不重试
        producer.setRetryTimesWhenSendAsyncFailed(0);
        producer.setRetryTimesWhenSendFailed(0);
        // 优化配置
        producer.setSendMsgTimeout(1000);              // 1秒超时
        producer.setMaxMessageSize(4 * 1024 * 1024);   // 4MB最大消息
        producer.setCompressMsgBodyOverHowmuch(4096);  // 超过4KB压缩
        producer.setRetryAnotherBrokerWhenNotStoreOK(false); // 不切换Broker
        RocketMQTemplate template = new RocketMQTemplate();
        template.setProducer(producer);

        log.info("IM RocketMQTemplate created with no-retry producer");

        return template;
    }


}