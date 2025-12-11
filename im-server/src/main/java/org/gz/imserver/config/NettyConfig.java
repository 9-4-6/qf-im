package org.gz.imserver.config;

import org.gz.imserver.manager.UserInstanceBindComponent;
import org.gz.imserver.netty.ChatServer;
import org.gz.qfinfra.rocketmq.producer.RocketmqProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 17853
 */
@Configuration
public class NettyConfig {

    @Bean
    public ChatServer chatServer(NettyProperties nettyProperties, RocketmqProducer rocketmqProducer,
                                 UserInstanceBindComponent userInstanceBindComponent) {
        return new ChatServer(nettyProperties, rocketmqProducer,userInstanceBindComponent);
    }
}
