package com.jim.Partner_Match.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    /**
     *
     */
    private Long id;

    /**
     * 昵称
     */
    private String username;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 登录头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 角色
     */
    private Integer userRole;

    /**
     * 标签
     */
    private String tags;

    /**
     * 自我介绍
     */
    private String profile;

    /**
     * 格式化后的时间
    */
    private String createTimeStr;
}
