package com.jim.Campus_Team.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 *
 * 作者:
 * 日期: 2024-05-23 11:46
 */

@Component
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${RedisKey.temp_Id}")
    private String temp_Id;

    @Scheduled(cron = "0 0 0 * * *")
    public void doCacheRecommendUser() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);

        String redisKey = String.format("user:recommend:%s", temp_Id);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 写缓存
        System.out.println("定时任务执行......................");
        try {
            valueOperations.set(redisKey, userPage.getRecords(), 24, TimeUnit.HOURS);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}