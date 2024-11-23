package com.jim.Campus_Team.contant;

/**
 * @author Jim_Lam
 * @description RedisKeyConstant
 */
public interface RedisKeyConstant {
    /**
     * 帖子点赞 key 前缀
     */
    String POST_LIKED_KEY_PREFIX = "post:liked:";

    /**
     * 评论点赞 key 前缀
     */
    String COMMENT_LIKED_KEY_PREFIX = "comment:liked:";
    /**
     * 帖子类型
     */
    int POST_TYPE = 1;
    /**
     * 评论类型
     */
    int COMMENT_TYPE = 2;
}