package org.gz.imcommon.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author 17853
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
@ConditionalOnClass(RocketMQTemplate.class)
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMqConfig {

    /**
     * 即时通讯专用Template
     */
    @Bean("rocketMqImTemplate")
    public RocketMQTemplate rocketMqImTemplate(RocketMQProperties rocketMqProperties) {
        log.info("Initializing IM no-retry RocketMQ producer...");
        // 使用特定的Producer Group
        DefaultMQProducer producer = new DefaultMQProducer("IM_NO_RETRY_GROUP");
        // 复用starter配置的name-server（也可以继续用@Value，二选一）
        producer.setNamesrvAddr(rocketMqProperties.getNameServer());
        // 核心配置：不重试
        producer.setRetryTimesWhenSendAsyncFailed(0);
        producer.setRetryTimesWhenSendFailed(0);
        // 优化配置
        // 2秒超时
        producer.setSendMsgTimeout(2000);
        // 4MB最大消息
        producer.setMaxMessageSize(4 * 1024 * 1024);
        // 超过4KB压缩
        producer.setCompressMsgBodyOverHowmuch(4096);
        // 不切换Broker
        producer.setRetryAnotherBrokerWhenNotStoreOK(false);
        RocketMQTemplate template = new RocketMQTemplate();
        template.setProducer(producer);

        log.info("IM RocketMQTemplate created with no-retry producer");

        return template;
    }


}