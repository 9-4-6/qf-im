package org.gz.imserver.netty;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author guozhong
 * @date 2025/11/18
 */
@Component
@Slf4j
public class NettyServerRunner implements CommandLineRunner , DisposableBean {

    @Resource
    private ChatServer chatServer;

    @Override
    public void run(String... args) throws MQClientException {
        chatServer.init();
    }

    @Override
    public void destroy() throws Exception {
        // Spring 容器关闭时，关闭 Netty 服务
        if (chatServer != null && chatServer.isRunning()) {
            chatServer.shutdown();
        }
    }
}
