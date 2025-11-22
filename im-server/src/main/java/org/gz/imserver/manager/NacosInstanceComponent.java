package org.gz.imserver.manager;

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gz.imserver.utils.InstanceIdGeneratorUtil;
import org.springframework.stereotype.Component;
/**
 * Nacos 实例 ID 工具类，全局维护当前实例的 ID
 */
@Component
@Slf4j
public class NacosInstanceComponent {
    /**
     * 当前实例的 Nacos 实例 ID
     */
    private static String instanceId;

    @Resource
    private NacosRegistration nacosRegistration;

    @PostConstruct
    public void init() {
        // 1. 优先获取 Nacos 自动生成的实例 ID
        instanceId = nacosRegistration.getInstanceId();
        if (instanceId != null && !instanceId.isEmpty()) {
            System.out.println("【实例初始化】当前 Nacos 实例 ID：" + instanceId);
            return;
        }

        // 2. 兜底：获取 IP 和端口
        String host = nacosRegistration.getHost();
        int port = nacosRegistration.getPort();
        if (host == null || host.isEmpty() || port <= 0) {
            throw new RuntimeException("实例 IP 或端口获取失败，无法生成兜底实例 ID");
        }

        // 3. 使用方案1生成兜底实例 ID（IP+端口+PID）
        instanceId = InstanceIdGeneratorUtil.generateByIpPortPid(host, port);

        log.info("【实例初始化】Nacos 实例 ID 未获取到，使用兜底规则生成:{}" + instanceId);
    }

    /**
     * 获取当前实例 ID
     */
    public static String getInstanceId() {
        return instanceId;
    }
}
