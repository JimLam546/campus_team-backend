package com.jim.Campus_Team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.entity.domain.PostComments;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.AddCommentRequest;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.PostCommentsService;
import com.jim.Campus_Team.mapper.PostCommentsMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author Jim_Lam
* @description 针对表【post_comments(帖子评论)】的数据库操作Service实现
* @createDate 2024-11-21 15:44:40
*/
@Service
public class PostCommentsServiceImpl extends ServiceImpl<PostCommentsMapper, PostComments>
    implements PostCommentsService{

    @Override
    public boolean publishComment(AddCommentRequest addCommentRequest, User loginUser) {
        if (addCommentRequest.getUserId() < 1 || addCommentRequest.getPostId() < 1) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        if (StringUtils.isBlank(addCommentRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        PostComments postComments = BeanUtil.copyProperties(addCommentRequest, PostComments.class);
        int insert = this.getBaseMapper().insert(postComments);
        return insert >= 1;

    }
}




