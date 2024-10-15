package com.jim.Campus_Team.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jim.Campus_Team.entity.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
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

    @Test
    public void importUser() {
        String file = "E:\\项目\\伙伴匹配系统\\campus_team-backend\\src\\main\\resources\\test.xlsx";

        EasyExcel.read(file, User.class, new ReadListener<User>() {
            /**
             * 单次缓存的数据量
             */
            public static final int BATCH_COUNT = 100;
            /**
             *临时存储
             */
            private List<User> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

            @Override
            public void invoke(User data, AnalysisContext context) {
                cachedDataList.add(data);
                System.out.println(data);
                if (cachedDataList.size() >= BATCH_COUNT) {
                    saveData();
                    // 存储完成清理 list
                    cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                saveData();
            }

            /**
             * 加上存储数据库
             */
            private void saveData() {
                log.info("{}条数据，开始存储数据库！", cachedDataList.size());
                log.info("存储数据库成功！");
            }
        }).sheet().doRead();
    }
}