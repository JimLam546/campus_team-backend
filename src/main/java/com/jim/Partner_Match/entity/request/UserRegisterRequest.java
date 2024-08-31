package com.jim.Partner_Match.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable{
    private static final long serialVersionUID = -206797676340717454L;
    /**
     * 账户
     */
    private String userAccount;
    /**
     * 昵称
     */
    private String username;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 确认密码
     */
    private String checkPassword;
}
