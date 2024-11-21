package com.jim.Campus_Team.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Jim_Lam
 * @description CommentVO
 */

@Data
public class CommentVO implements Serializable {

    private static final long serialVersionUID = -7382529537664151759L;

    private Long id;

    /**
     * 评论用户
     */
    private UserVO userVO;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 子评论
     */
    private List<CommentVO> subCommentList;

    /**
     * 是否已经点赞
     */
    private boolean myLiked;

    /**
     * 点赞量
     */
    private Long likedNum;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Date createTime;

}