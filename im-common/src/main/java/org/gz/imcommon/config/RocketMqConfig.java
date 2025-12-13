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

    // 持有生产者实例，用于销毁时关闭
    private DefaultMQProducer imNoRetryProducer;

    /**
     * 即时通讯专用生产者 - 不重试，快速失败
     * 核心修改：移除手动start()，交给rocketmq-spring-boot-starter自动管理
     */
    @Bean("imNoRetryProducer")
    public DefaultMQProducer imNoRetryProducer(
            RocketMQProperties rocketMQProperties) {

        log.info("Initializing IM no-retry RocketMQ producer...");
        try {
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
            // 关键：移除手动start()！交给rocketmq-spring-boot-starter自动启动
            this.imNoRetryProducer = producer;

            log.info("IM no-retry RocketMQ producer initialized (will be started by starter): group={}",
                    producer.getProducerGroup());

            return producer;

        } catch (Exception e) {
            log.error("Failed to initialize IM no-retry RocketMQ producer", e);
            throw new RuntimeException("RocketMQ producer initialization failed", e);
        }
    }

    /**
     * 即时通讯专用Template
     * 补充@DependsOn：确保生产者Bean先初始化完成
     */
    @Bean("rocketMqImTemplate")
    @DependsOn("imNoRetryProducer")
    public RocketMQTemplate rocketMqImTemplate(
            @Qualifier("imNoRetryProducer") DefaultMQProducer imNoRetryProducer) {

        RocketMQTemplate template = new RocketMQTemplate();
        template.setProducer(imNoRetryProducer);

        log.info("IM RocketMQTemplate created with no-retry producer");

        return template;
    }

    /**
     * 销毁钩子：服务停止时关闭生产者（避免资源泄漏）
     * 注：starter也会自动关闭，但手动补充更稳妥（防止极端情况）
     */
    @PreDestroy
    public void destroyProducer() {
        if (this.imNoRetryProducer != null && this.imNoRetryProducer.getDefaultMQProducerImpl() != null) {
            log.info("Shutting down IM no-retry RocketMQ producer: group={}",
                    this.imNoRetryProducer.getProducerGroup());
            this.imNoRetryProducer.shutdown();
            log.info("IM no-retry RocketMQ producer shut down successfully");
        }
    }
}