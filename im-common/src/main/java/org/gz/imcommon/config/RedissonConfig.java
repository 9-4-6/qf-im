package org.gz.imcommon.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.redis.redisson.enabled", havingValue = "true")
public class RedissonConfig {
    @Value("${spring.redis.redisson.mode}")
    private String mode;

    /**
     * 仅仅用于sentinel模式。
     */
    @Value("${spring.redis.redisson.masterName:}")
    private String masterName;

    @Value("${spring.redis.redisson.address}")
    private String address;

    @Value("${spring.redis.redisson.password:}")
    private String password;

    /**
     * 数据库默认0
     */
    @Value("${spring.redis.redisson.database:0}")
    private Integer database;

    @Bean
    public RedissonClient redissonClient() {
        if (StringUtils.isBlank(password)) {
            password = null;
        }
        Config config = new Config();
        if ("single".equals(mode)) {
            config.useSingleServer()
                    .setDatabase(database)
                    .setPassword(password)
                    .setAddress(address);
        } else if ("cluster".equals(mode)) {
            String[] clusterAddresses = address.split(",");
            config.useClusterServers()
                    //集群模式不支持多个数据库概念，默认db 0
                    .setPassword(password)
                    .addNodeAddress(clusterAddresses);
        } else if ("sentinel".equals(mode)) {
            String[] sentinelAddresses = address.split(",");
            config.useSentinelServers()
                    .setDatabase(database)
                    .setPassword(password)
                    .setMasterName(masterName)
                    .addSentinelAddress(sentinelAddresses);
        } else if ("master-slave".equals(mode)) {
            String[] masterSlaveAddresses = address.split(",");
            if (masterSlaveAddresses.length == 1) {
                throw new IllegalArgumentException(
                        "redis.redisson.address MUST have multiple redis addresses for master-slave mode.");
            }
            String[] slaveAddresses = new String[masterSlaveAddresses.length - 1];
            System.arraycopy(masterSlaveAddresses, 1, slaveAddresses, 0, slaveAddresses.length);
            config.useMasterSlaveServers()
                    .setDatabase(database)
                    .setPassword(password)
                    .setMasterAddress(masterSlaveAddresses[0])
                    .addSlaveAddress(slaveAddresses);
        } else {
            throw new IllegalArgumentException(mode);
        }
        return Redisson.create(config);
    }
}
