package com.jim.Campus_Team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.entity.domain.Post;
import com.jim.Campus_Team.entity.domain.PostComments;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.AddPostRequest;
import com.jim.Campus_Team.entity.request.PageRequest;
import com.jim.Campus_Team.entity.vo.CommentVO;
import com.jim.Campus_Team.entity.vo.PostVO;
import com.jim.Campus_Team.entity.vo.UserVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.PostCommentsService;
import com.jim.Campus_Team.service.PostService;
import com.jim.Campus_Team.mapper.PostMapper;
import com.jim.Campus_Team.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.jim.Campus_Team.contant.RedisKeyConstant.COMMENT_LIKED_KEY_PREFIX;
import static com.jim.Campus_Team.contant.RedisKeyConstant.POST_LIKED_KEY_PREFIX;

/**
* @author Jim_Lam
* @description 针对表【post(帖子表)】的数据库操作Service实现
* @createDate 2024-11-21 15:42:11
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

    @Resource
    private UserService userService;

    @Resource
    private PostCommentsService postCommentsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public List<PostVO> listPostByPage(PageRequest pageRequest, Long userId) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        if (pageNum < 1) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        if (pageSize >= 20) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "总量不能超过20");
        }
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.orderByDesc("createTime");
        List<Post> postList = this.getBaseMapper().selectPage(new Page<>(pageNum, pageSize), postQueryWrapper).getRecords();
        return postList.stream().map(post -> {
            PostVO postVO = BeanUtil.copyProperties(post, PostVO.class);
            UserVO userVO = BeanUtil.copyProperties(userService.getById(post.getUserId()), UserVO.class);
            postVO.setUserVO(userVO);
            // 检查是否已经点赞
            String key = POST_LIKED_KEY_PREFIX + post.getId();
            Boolean member = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
            postVO.setMyLiked(BooleanUtil.isTrue(member));
            // todo 目前只有一级帖子评论
            List<PostComments> postCommentsList = postCommentsService.lambdaQuery()
                    .eq(PostComments::getPostId, post.getId())
                    .list();
            List<CommentVO> commentVOList = postCommentsList.stream().map(postComments -> {
                // todo 没有子评论
                CommentVO commentVO = BeanUtil.copyProperties(postComments, CommentVO.class);
                User user = userService.getById(postComments.getUserId());
                commentVO.setUserVO(BeanUtil.copyProperties(user, UserVO.class));
                // 检查是否已经点赞
                String commentKey = COMMENT_LIKED_KEY_PREFIX + postComments.getPostId();
                Boolean member1 = stringRedisTemplate.opsForSet().isMember(commentKey, userId.toString());
                commentVO.setMyLiked(Boolean.TRUE.equals(member1));
                return commentVO;
            }).collect(Collectors.toList());
            postVO.setCommentVOList(commentVOList);
            return postVO;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean publishPost(AddPostRequest addPostRequest, User loginUser) {
        if (StringUtils.isBlank(addPostRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        if (StringUtils.isBlank(addPostRequest.getModule())) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        String imageList = null;
        if (!CollectionUtil.isEmpty(addPostRequest.getImageUrlList())) {
            imageList = String.join(";", addPostRequest.getImageUrlList());
        }
        Post post = BeanUtil.copyProperties(addPostRequest, Post.class);
        post.setImageUrl(imageList);
        int insert = this.getBaseMapper().insert(post);
        return insert >= 1;
    }
}




