package org.gz.imserver.manager;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gz.imcommon.constants.RedisConstant;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
/**
 * @author 17853
 */
@Component
@Slf4j
public class UserInstanceBindComponent {
    @Resource
    private RedissonClient redissonClient;

    /**
     * 用户上线：绑定用户 ID 与当前实例 ID
     */
    public void bindUser(Long userId,Integer brokerId) {
        RMap<Long, Integer> userInstanceHash = redissonClient.getMap(RedisConstant.USER_INSTANCE_HASH_KEY);
        userInstanceHash.put(userId, brokerId);
        log.info("【用户绑定】用户 ID:{},实例 ID:{}", userId, brokerId);
    }

    /**
     * 获取用户绑定的实例 ID
     */
    public Integer getInstanceByUser(Long userId) {
        RMap<Long, Integer> userInstanceHash = redissonClient.getMap(RedisConstant.USER_INSTANCE_HASH_KEY);
        return userInstanceHash.get(userId);
    }


}
