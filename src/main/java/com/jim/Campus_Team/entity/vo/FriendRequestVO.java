package com.jim.Campus_Team.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jim_Lam
 * @date 2024-10-27 22:18
 * @description FriendRequestVO
 */
@Data
public class FriendRequestVO implements Serializable {
    private static final long serialVersionUID = 5481659843907611945L;
    /**
     * 发送者
     */
    private String fromId;

    /**
     * 发送者名称
     */
    private String fromName;

    /**
     * 接收用户
     */
    private Long receiveId;

    /**
     * 接收用户名称
     */
    private Long receiveName;

    /**
     * 是否读取；0-未读，1-已读
     */
    private Integer isRead;

    /**
     * 状态：0-未处理；1-同意；2-拒绝
     */
    private Integer state;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private String createTime;
}