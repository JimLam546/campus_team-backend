package com.jim.Campus_Team.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jim_Lam
 * @date 2024-10-28 23:35
 * @description 接收方操作请求实体类
 */
@Data
public class OpsFriendRequest implements Serializable {
    private static final long serialVersionUID = -683020641456501073L;

    /**
     * 发送者
     */
    private Long fromId;
    /**
     * 是否读取；0-未读，1-已读
     */
    private Integer isRead;

    /**
     * 状态：0-未处理；1-同意；2-拒绝
     */
    private Integer state;
}