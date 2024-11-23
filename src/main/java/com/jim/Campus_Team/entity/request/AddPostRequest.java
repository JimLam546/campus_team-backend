package com.jim.Campus_Team.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jim_Lam
 * @description AddPostRequest
 */

@Data
public class AddPostRequest implements Serializable {
    private static final long serialVersionUID = -5519838250221980119L;

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
    private List<String> imageUrlList;
}