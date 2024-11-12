package com.jim.Campus_Team.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jim_Lam
 * @date 2024-11-05 22:30
 * @description ChatMessageVO
 */

@Data
public class ChatMessageVO implements Serializable {
    private static final long serialVersionUID = 4214166172695870353L;

    /**
     * 发送用户
     */
    private ChatUserVO fromUser;

    /**
     * 接收用户
     */
    private ChatUserVO toUser;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 内容
     */
    private String text;

    /**
     * 是不是自己的消息
     */
    private boolean myMessage = false;

    /**
     * 消息类型
     */
    private Integer chatType;

    /**
     * 是否管理员
     */
    private Boolean isAdmin = false;

    /**
     * 创建时间
     */
    private String createTime;
}
