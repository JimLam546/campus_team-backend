package com.jim.Campus_Team.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Jim_Lam
 * @date 2024-11-12 14:36
 * @description TeamChatVO
 */

@Data
public class TeamChatVO implements Serializable {

    private static final long serialVersionUID = 1858155710956579137L;

    /**
     * 队伍id
     */
    private Long id;

    /**
     * 最后一条消息的用户名
     */
    private String username;

    /**
     * 群聊名称
     */
    private String teamName;

    /**
     * 群头像
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