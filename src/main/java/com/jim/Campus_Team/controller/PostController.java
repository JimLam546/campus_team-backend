package com.jim.Campus_Team.controller;

import com.jim.Campus_Team.common.BaseResponse;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.common.ResultUtil;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.PageRequest;
import com.jim.Campus_Team.entity.vo.PostVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.PostService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;

import static com.jim.Campus_Team.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @author Jim_Lam
 * @date 2024-11-21 15:46
 * @description PostController
 */

@RestController
@RequestMapping("/post")
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 根据发布时间获取帖子
     * @param pageRequest 分页参数
     * @param httpSession 回话
     * @return 帖子列表
     */
    @GetMapping("/listPost")
    public BaseResponse<List<PostVO>> listPost(PageRequest pageRequest, HttpSession httpSession) {
        User loginUser = (User) httpSession.getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (pageRequest == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        List<PostVO> postVOList = postService.listPostByPage(pageRequest, loginUser.getId());
        return ResultUtil.success(postVOList);
    }


}