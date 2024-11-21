package com.jim.Campus_Team.service;

import com.jim.Campus_Team.entity.domain.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jim.Campus_Team.entity.request.PageRequest;
import com.jim.Campus_Team.entity.vo.PostVO;

import java.util.List;

/**
* @author Jim_Lam
* @description 针对表【post(帖子表)】的数据库操作Service
* @createDate 2024-11-21 15:42:11
*/
public interface PostService extends IService<Post> {

    List<PostVO> listPostByPage(PageRequest pageRequest, Long userId);
}
