package org.gz.imserver.config;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.gz.imcommon.config.RocketMqConfig;
import org.gz.imserver.manager.UserInstanceBindComponent;
import org.gz.imserver.netty.ChatServer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author 17853
 */
@Configuration
@DependsOn("rocketMqImTemplate")
public class NettyConfig {

    @Bean
    public ChatServer chatServer(NettyProperties nettyProperties,
                                 @Qualifier("rocketMqImTemplate")RocketMQTemplate rocketMqImTemplate,
                                 UserInstanceBindComponent userInstanceBindComponent) {
        return new ChatServer(nettyProperties, rocketMqImTemplate,userInstanceBindComponent);
    }
}
