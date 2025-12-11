package org.gz.imserver.manager;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gz.imcommon.constants.RedisConstant;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * @author 17853
 */
@Component
@Slf4j
public class UserInstanceBindComponent {
    @Value("${im.rocketmq.consumer-group.prefix}:${server.port}")
    private String instanceId;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 用户上线：绑定用户 ID 与当前实例 ID
     */
    public void bindUser(Long userId) {
        RMap<Long, String> userInstanceHash = redissonClient.getMap(RedisConstant.USER_INSTANCE_HASH_KEY);
        userInstanceHash.put(userId, instanceId);
        log.info("【用户绑定】用户 ID:{},实例 ID:{}", userId, instanceId);
    }

    /**
     * 获取用户绑定的实例 ID
     */
    public String getInstanceByUser(Long userId) {
        RMap<Long, String> userInstanceHash = redissonClient.getMap(RedisConstant.USER_INSTANCE_HASH_KEY);
        return userInstanceHash.get(userId);
    }

    /**
     * 用户主动下线：解除单个用户的绑定关系
     */
    public void unbindUser(Long userId) {
        String instanceId = getInstanceByUser(userId);
        if (instanceId == null) {
            return;
        }
        RMap<Long, String> userInstanceHash = redissonClient.getMap(RedisConstant.USER_INSTANCE_HASH_KEY);
        userInstanceHash.remove(userId);
        log.info("【用户下线】用户 ID:{},实例 ID:{}", userId, instanceId);
    }



}
