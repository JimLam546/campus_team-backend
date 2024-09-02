package com.jim.Campus_Team;

import com.jim.Campus_Team.entity.domain.Team;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.service.TeamService;
import com.jim.Campus_Team.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
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
        ArrayList<User> userList = new ArrayList<>();
        int i = 0;
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
        for(int j = 0; j < 10; j++)
            userService.saveBatch(userList);
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
}
