package org.gz.imserver.task;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import jakarta.annotation.Resource;
import org.gz.imserver.manager.UserInstanceBindComponent;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 定时任务：清理 Nacos 失效实例的用户-实例绑定关系
 */
@Component
public class InstanceHealthCheckTask {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Resource
    private UserInstanceBindComponent userInstanceBindComponent;

    private static final String INSTANCE_USER_SET_PREFIX = "instance-users:";

    /**
     * 每 30 秒检查一次失效实例（可根据业务调整）
     */
    @Scheduled(fixedRate = 30000)
    public void cleanInvalidInstanceBind() throws Exception {
        // 1. 获取 Nacos 中当前服务的所有健康实例
        NamingService namingService = NamingFactory.createNamingService(nacosDiscoveryProperties.getServerAddr());
        String serviceId = nacosDiscoveryProperties.getService();
        String group = nacosDiscoveryProperties.getGroup();
        List<Instance> healthyInstances = namingService.selectInstances(serviceId, group, true);
        Set<String> healthyInstanceIdSet = healthyInstances.stream()
                .map(Instance::getInstanceId)
                .collect(java.util.stream.Collectors.toSet());

        // 2. 获取 Redis 中所有实例的用户 Set Key
        Iterable<String> keysByPattern = redissonClient.getKeys()
                .getKeysByPattern(INSTANCE_USER_SET_PREFIX + "*");
        for (String instanceUserKey : keysByPattern) {
            // 提取实例 ID
            String instanceId = instanceUserKey.replace(INSTANCE_USER_SET_PREFIX, "");
            // 3. 若实例不在健康列表中，清理其绑定关系
            if (!healthyInstanceIdSet.contains(instanceId)) {
                // 调用清理方法
                userInstanceBindComponent.clearAllUserBind(instanceId);
                // 需改造 UserInstanceBindService 支持指定实例 ID 清理
                System.out.println("【定时清理】失效实例 " + instanceId + " 的用户绑定关系已清理");
            }
        }
    }
}
