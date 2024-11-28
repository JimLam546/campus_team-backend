package com.jim.Campus_Team.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Jim_Lam
 * @description PostVO
 */

@Data
public class PostVO implements Serializable {
    private static final long serialVersionUID = 5895124925894523170L;

    private Long id;

    /**
     * 发布用户
     */
    private UserVO userVO;

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
    private List<String> imageUrl;

    /**
     * 是否已经点赞
     */
    private boolean myLiked;

    /**
     * 评论
     */
    private List<CommentVO> commentVOList;

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