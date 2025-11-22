package org.gz.imserver.manager;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class UserInstanceBindComponent {
    /**
     * 用户-实例绑定关系的 Hash Key
     */
    private static final String USER_INSTANCE_HASH_KEY = "user-instance";
    /**
     * 实例-用户列表的 Set Key 前缀
     */
    private static final String INSTANCE_USER_SET_PREFIX = "instance-users:";

    @Resource
    private RedissonClient redissonClient;

    /**
     * 用户上线：绑定用户 ID 与当前实例 ID
     */
    public void bindUser(String userId) {
        String instanceId = NacosInstanceComponent.getInstanceId();
        // 1. 存储用户-实例关系（Hash）
        RMap<String, String> userInstanceHash = redissonClient.getMap(USER_INSTANCE_HASH_KEY);
        userInstanceHash.put(userId, instanceId);
        // 2. 存储实例-用户关系（Set），便于下线时批量清理
        String instanceUserKey = INSTANCE_USER_SET_PREFIX + instanceId;
        RSet<String> instanceUserSet = redissonClient.getSet(instanceUserKey);
        instanceUserSet.add(userId);
        System.out.println("【用户绑定】用户 ID：" + userId + "，实例 ID：" + instanceId);
    }

    /**
     * 获取用户绑定的实例 ID
     */
    public String getInstanceByUser(String userId) {
        RMap<String, String> userInstanceHash = redissonClient.getMap(USER_INSTANCE_HASH_KEY);
        return userInstanceHash.get(userId);
    }

    /**
     * 用户主动下线：解除单个用户的绑定关系
     */
    public void unbindUser(String userId) {
        String instanceId = getInstanceByUser(userId);
        if (instanceId == null) {
            return;
        }
        // 1. 删除用户-实例关系
        RMap<String, String> userInstanceHash = redissonClient.getMap(USER_INSTANCE_HASH_KEY);
        userInstanceHash.remove(userId);
        // 2. 删除实例-用户关系
        String instanceUserKey = INSTANCE_USER_SET_PREFIX + instanceId;
        RSet<String> instanceUserSet = redissonClient.getSet(instanceUserKey);
        instanceUserSet.remove(userId);
        System.out.println("【用户下线】用户 ID：" + userId + "，实例 ID：" + instanceId);
    }

    /**
     * 根据实例绑定的所有用户关系
     */
    public void clearAllUserBind(String instanceId) {
        String realInstanceId = StringUtils.isNotBlank(instanceId) ? instanceId : NacosInstanceComponent.getInstanceId();
        String instanceUserKey = INSTANCE_USER_SET_PREFIX + realInstanceId;
        RSet<String> instanceUserSet = redissonClient.getSet(instanceUserKey);
        // 1. 获取当前实例绑定的所有用户 ID
        Set<String> userIdSet = instanceUserSet.readAll();
        if (userIdSet.isEmpty()) {
            return;
        }
        // 2. 批量删除用户-实例关系
        RMap<String, String> userInstanceHash = redissonClient.getMap(USER_INSTANCE_HASH_KEY);
        userInstanceHash.fastRemove(userIdSet.toArray(new String[0]));
        // 3. 删除实例-用户关系 Set
        instanceUserSet.delete();
        System.out.println("【实例下线】清理实例 " + realInstanceId + " 绑定的用户：" + userIdSet);
    }

}
