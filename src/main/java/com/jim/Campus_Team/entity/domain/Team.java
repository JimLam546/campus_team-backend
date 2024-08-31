package com.jim.Partner_Match.entity.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最多人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 队伍创建人
     */
    private Long userId;

    /**
     * 队伍加入密码
     */
    private String teamPassword;

    /**
     * 队伍状态(0-公开，1-加密，2-private，3-过期)
     */
    private Integer teamStatus;

    /**
     * 队伍创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 队伍头像
     */
    private String avatarUrl;

    /**
     * 逻辑删除(0-存在，1-删除)
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}