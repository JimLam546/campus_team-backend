package com.jim.Campus_Team.entity.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Jim_Lam
 * @date 2024-10-27 21:56
 * @description AddFriendRequest
 */
@Data
public class AddFriendRequest implements Serializable {
    private static final long serialVersionUID = -5983121027851058561L;
    /**
     * 发送用户
     */
    private Long fromId;

    /**
     * 接收用户
     */
    private Long receiveId;

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
}