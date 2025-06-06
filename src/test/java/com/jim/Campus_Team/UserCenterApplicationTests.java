package com.jim.Campus_Team;

import com.jim.Campus_Team.entity.domain.Team;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.service.TeamService;
import com.jim.Campus_Team.service.UserService;
import jodd.util.collection.CompositeEnumeration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@SpringBootTest
@Slf4j
class UserCenterApplicationTests {

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TeamService teamService;

    @Test
    public void testDate() {
        Team team = teamService.getById(9);
        Date expireTime = team.getExpireTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(expireTime));
    }

    @Test
    void contextLoads() {
        List<String> tags = Arrays.asList("java", "python");
        // List<User> userList = userService.searchUsersByTags(tags);
        // System.out.println(userList);
    }


    @Test
    void importUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int i = 0;
        ExecutorService executor = new ThreadPoolExecutor(5,
                10,
                1000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100000));
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for(int j = 0; j < 100; j++) {
            List<User> userList = new ArrayList<>(10000);
            do {
                i++;
                User user = new User();
                user.setUsername("testUser");
                user.setUserAccount("test");
                user.setGender(0);
                user.setUserPassword("123456789");
                user.setPhone("123456");
                user.setEmail("123.com");
                user.setIsVaild(0);
                user.setIsDelete(0);
                user.setUserRole(0);
                user.setTags("[\"python\"]");
                user.setProfile("hello");
                userList.add(user);
            } while (i % 10000 != 0);
            CompletableFuture<Void> future =
                    CompletableFuture.runAsync(() -> userService.saveBatch(userList), executor);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    void testRedis() {
        int num = 1;
        stringRedisTemplate.opsForValue().set("num", String.valueOf(num));
        String s = stringRedisTemplate.opsForValue().get("num");
        System.out.println(s);
        stringRedisTemplate.opsForValue().increment("num", 2);
        s = stringRedisTemplate.opsForValue().get("num");
        System.out.println(s);
    }

    @Test
    public void t1() {
        String host = "192.168.1.12";
        String port = "1234";
        String format = String.format("ip地址: %s:%s", host, port);
        System.out.println(format);
    }

    @Test
    public void testAdd() {
        String key = "test";
        stringRedisTemplate.opsForValue().setIfAbsent(key, "1");
        log.info("Redis 初始时，key = " + stringRedisTemplate.opsForValue().get(key));
        for (int i = 0; i < 10; i++) {
            log.info("执行用户id = " + stringRedisTemplate.opsForValue().get(key) + " 的用户计算");
            stringRedisTemplate.opsForValue().increment(key);
        }
    }
}
