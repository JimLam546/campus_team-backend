package com.jim.Campus_Team.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jim_Lam
 * @description AddCommentRequest
 */

@Data
public class AddCommentRequest implements Serializable {

    private static final long serialVersionUID = 5538042832625089609L;

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
}