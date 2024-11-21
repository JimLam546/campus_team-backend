package com.jim.Campus_Team.entity.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 帖子评论
 * @TableName post_comments
 */
@TableName(value ="post_comments")
@Data
public class PostComments implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评论用户id
     */
    private Long userId;

    /**
     * 帖子id
     */
    private Long postId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论id
     */
    private Long parentId;

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