package com.jim.Campus_Team.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Test
    public void testInsert() {
    }

//    @Test
//    void userRegister() {
//        String userAccount = "123456789";
//        String userPassword = "";
//        String checkPassword = "123456";
//        long l = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, l);
//        userAccount = "123";
//        l = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, l);
//        userAccount = "Hello";
//        userPassword = "123456";
//        l = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, l);
//        userAccount = "123456789";
//        userPassword = "123456798";
//        checkPassword = "123456798";
//        l = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, l);
//        userAccount = "account.";
//        userPassword = "123456";
//        checkPassword = "123456";
//        l = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, l);
//        userAccount = "account";
//        userPassword = "123456";
//        checkPassword = "321456";
//        l = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, l);
//        userPassword = "123456789";
//        checkPassword = "123456789";
//        l = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(0, l);
//    }
    /**
     * JSON随机抽取
     */
    @Test
    public static void main(String[] args) {
        String json = "[\"java\",\"女\",\"男\",\"大一\",\"大二\",\"大三\",\"大四\",\"研究生\",\"创新创业\",\"数学建模\",\"电子设计\",\"篮球\",\"足球\",\"羽毛球\",\"乒乓球\",\"游戏\",\"摄影\",\"音乐\",\"电影\",\"旅游\",\"python\",\"c++\",\"GO\",\"前端\"]";
        Gson gson = new Gson();
        ArrayList<String> arrayList =(ArrayList<String>) gson.fromJson(json, new TypeToken<ArrayList<String>>() {}.getType());
        for (int i = 0; i < 500; i++) {
            int max = arrayList.size();
            int tagNum = RandomUtil.randomInt(1, 8);
            ArrayList<String> list = new ArrayList<>();
            // 生成一份原数据
            ArrayList<String> allTagList =(ArrayList<String>) gson.fromJson(json, new TypeToken<ArrayList<String>>() {}.getType());
            for (int j = 0; j < tagNum; j++) {
                // 随机抽取的索引
                int index = RandomUtil.randomInt(1, max, true, false);
                list.add(allTagList.get(index));
                allTagList.remove(index);
                max--;
            }
            String jsonStr = gson.toJson(list);
            System.out.println(jsonStr);
        }
        // 随机抽取的个数

    }
}