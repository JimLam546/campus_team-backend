package com.jim.Campus_Team.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jim_Lam
 * @date 2024-11-05 22:27
 * @description ChatUserVO
 */
@Data
public class ChatUserVO implements Serializable {
    private static final long serialVersionUID = 9157612011248259424L;
    /**
     * 用户id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;
}