package com.jim.Campus_Team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.entity.domain.PostComments;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.AddCommentRequest;
import com.jim.Campus_Team.entity.vo.CommentVO;
import com.jim.Campus_Team.entity.vo.UserVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.PostCommentsService;
import com.jim.Campus_Team.mapper.PostCommentsMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author Jim_Lam
* @description 针对表【post_comments(帖子评论)】的数据库操作Service实现
* @createDate 2024-11-21 15:44:40
*/
@Service
public class PostCommentsServiceImpl extends ServiceImpl<PostCommentsMapper, PostComments>
    implements PostCommentsService{

    @Override
    public CommentVO publishComment(AddCommentRequest addCommentRequest, User loginUser) {
        if (addCommentRequest.getPostId() < 1) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        if (StringUtils.isBlank(addCommentRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        PostComments postComments = BeanUtil.copyProperties(addCommentRequest, PostComments.class);
        postComments.setUserId(loginUser.getId());
        int insert = this.getBaseMapper().insert(postComments);
        if (insert == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        CommentVO commentVO = new CommentVO();
        commentVO.setId(postComments.getId());
        commentVO.setUserVO(BeanUtil.copyProperties(loginUser, UserVO.class));
        commentVO.setContent(addCommentRequest.getContent());
        commentVO.setCreateTime(new Date());
        commentVO.setLikedNum(0L);
        return commentVO;
    }
}




