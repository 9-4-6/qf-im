package org.gz.imserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author guozhong
 * @date 2025/11/18
 */
@Data
@Component
@ConfigurationProperties(prefix = "netty.server")
public class NettyConfig {
    // tcp 绑定的端口号
    private Integer tcpPort;
    // boss线程 默认=1
    private Integer bossThreadSize;
    //work线程
    private Integer workThreadSize;
    //心跳超时时间 单位毫秒
    private Long heartBeatTime;
}
