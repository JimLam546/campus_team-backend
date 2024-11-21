package com.jim.Campus_Team.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Campus_Team.entity.domain.PostComments;
import com.jim.Campus_Team.service.PostCommentsService;
import com.jim.Campus_Team.mapper.PostCommentsMapper;
import org.springframework.stereotype.Service;

/**
* @author Jim_Lam
* @description 针对表【post_comments(帖子评论)】的数据库操作Service实现
* @createDate 2024-11-21 15:44:40
*/
@Service
public class PostCommentsServiceImpl extends ServiceImpl<PostCommentsMapper, PostComments>
    implements PostCommentsService{

}




