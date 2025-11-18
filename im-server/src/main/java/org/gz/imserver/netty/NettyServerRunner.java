package org.gz.imserver.netty;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gz.imcommon.config.TcpConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author guozhong
 * @date 2025/11/18
 */
@Component
@Slf4j
public class NettyServerRunner implements CommandLineRunner {
    @Resource
    private  TcpConfig tcpConfig;

    @Override
    public void run(String... args)  {
        new Thread(() -> {
            try {
                new ChatServer(tcpConfig).start();
                log.info("Netty Chat Server started successfully on port: {}", tcpConfig.getTcpPort());
            } catch (Exception e) {
                log.error("Netty Server start failed: ", e);
            }
        }, "netty-server-thread").start();
    }
}
