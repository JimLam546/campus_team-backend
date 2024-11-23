package com.jim.Campus_Team.service;

import com.jim.Campus_Team.entity.domain.PostComments;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.AddCommentRequest;

/**
* @author Jim_Lam
* @description 针对表【post_comments(帖子评论)】的数据库操作Service
* @createDate 2024-11-21 15:44:40
*/
public interface PostCommentsService extends IService<PostComments> {

    boolean publishComment(AddCommentRequest addCommentRequest, User loginUser);
}
