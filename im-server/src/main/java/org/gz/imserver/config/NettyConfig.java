package org.gz.imserver.config;

import org.gz.imserver.netty.ChatServer;
import org.gz.qfinfra.rocketmq.producer.RocketmqProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyConfig {
    /**
     * 将 ChatServer 注册为 Spring Bean，注入 NettyConfig 和 RocketmqProducer
     */
    @Bean
    public ChatServer chatServer(NettyProperties nettyProperties,  RocketmqProducer rocketmqProducer) {
        return new ChatServer(nettyProperties, rocketmqProducer);
    }
}
