package com.jim.Campus_Team.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Jim_Lam
 * @date 2024-11-07 15:56
 * @description PrivateChatUserVO
 */

@Data
public class PrivateChatUserVO implements Serializable {
    private static final long serialVersionUID = -8993169747183349825L;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 最后一条消息
     */
    private String lastMessage;

    /**
     * 未读消息数量
     */
    private Integer noReadNum;

    /**
     * 最后消息日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Date lastMessageDate;
}