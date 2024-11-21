package com.jim.Campus_Team.entity.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 帖子表
 * @TableName post
 */
@TableName(value ="post")
@Data
public class Post implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 内容模块
     */
    private String module;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 状态：0-发布；1-下架
     */
    private Integer state;

    /**
     * 点赞量
     */
    private Long likedNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除：0-存在；1-删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}