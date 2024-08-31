package com.jim.Partner_Match.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 作者:
 * 日期: 2024-05-28 15:41
 */

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String host;
    private String port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String redisAddr = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(redisAddr).setDatabase(1);
        // 返回实例
        return Redisson.create(config);
    }
}