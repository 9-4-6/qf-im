package org.gz.imserver.config;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.gz.imserver.manager.UserInstanceBindComponent;
import org.gz.imserver.netty.ChatServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 17853
 */
@Configuration
public class NettyConfig {

    @Bean
    public ChatServer chatServer(NettyProperties nettyProperties, RocketMQTemplate rocketMqTemplate,
                                 UserInstanceBindComponent userInstanceBindComponent) {
        return new ChatServer(nettyProperties, rocketMqTemplate,userInstanceBindComponent);
    }
}
