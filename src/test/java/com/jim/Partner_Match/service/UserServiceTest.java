package com.jim.Partner_Match.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
}