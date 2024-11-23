package com.jim.Campus_Team.controller;

import cn.hutool.core.util.BooleanUtil;
import com.jim.Campus_Team.common.BaseResponse;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.common.OSSUploadUtil;
import com.jim.Campus_Team.common.ResultUtil;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.AddCommentRequest;
import com.jim.Campus_Team.entity.request.AddPostRequest;
import com.jim.Campus_Team.entity.request.PageRequest;
import com.jim.Campus_Team.entity.vo.PostVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.PostCommentsService;
import com.jim.Campus_Team.service.PostService;
import io.swagger.annotations.Api;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.jim.Campus_Team.contant.RedisKeyConstant.*;
import static com.jim.Campus_Team.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @author Jim_Lam
 * @date 2024-11-21 15:46
 * @description PostController
 */

@RestController
@RequestMapping("/post")
@Api(tags = "帖子模块")
@CrossOrigin(originPatterns = {"http://localhost:5173", "http://47.115.163.154:5173"}, allowCredentials = "true")
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private PostCommentsService postCommentsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根据发布时间获取帖子
     * @param pageRequest 分页参数
     * @param request 回话
     * @return 帖子列表
     */
    @PostMapping("/listPost")
    public BaseResponse<List<PostVO>> listPost(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (pageRequest == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        List<PostVO> postVOList = postService.listPostByPage(pageRequest, loginUser.getId());
        return ResultUtil.success(postVOList);
    }

    /**
     * 点赞
     * @param request 会话
     * @param id 评论或帖子id
     * @return 结果
     */
    @GetMapping("/liked/{type}/{id}")
    public BaseResponse<Boolean> liked(HttpServletRequest request, @PathVariable("id") Long id, @PathVariable int type) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        // 帖子类型
        if (POST_TYPE == type) {
            String key = POST_LIKED_KEY_PREFIX + id;
            Boolean member = stringRedisTemplate.opsForSet().isMember(key, loginUser.getId().toString());
            if (BooleanUtil.isFalse(member)) {
                // 没有点赞则点赞
                stringRedisTemplate.opsForSet().add(key, loginUser.getId().toString());
                boolean update = postService.update().eq("id", id).setSql("likedNum = likedNum + 1").update();
                if (!update) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
            } else {
                // 点赞了则取消点赞
                stringRedisTemplate.opsForSet().remove(key, loginUser.getId().toString());
                boolean update = postService.update().eq("id", id).setSql("likedNum = likedNum - 1").update();
                if (!update) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
            }
            return ResultUtil.success(true);
        } else if (COMMENT_TYPE == type) {
            String key = COMMENT_LIKED_KEY_PREFIX + id;
            Boolean member = stringRedisTemplate.opsForSet().isMember(key, loginUser.getId().toString());
            if (BooleanUtil.isFalse(member)) {
                // 没有点赞则点赞
                stringRedisTemplate.opsForSet().add(key, loginUser.getId().toString());
                boolean update = postCommentsService.update().eq("id", id).setSql("likedNum = likedNum + 1").update();
                if (!update) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
            } else {
                // 点赞了则取消点赞
                stringRedisTemplate.opsForSet().remove(key, loginUser.getId().toString());
                boolean update = postCommentsService.update().eq("id", id).setSql("likedNum = likedNum - 1").update();
                if (!update) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
            }
            return ResultUtil.success(true);
        }
        return ResultUtil.error(40500, "不存在该类型", "不存在该类型");
    }

    /**
     * 发布帖子
     * @param request 会话请求
     * @param addPostRequest 添加帖子请求
     * @return
     */
    @PostMapping
    public BaseResponse<Boolean> publishPost(HttpServletRequest request, @RequestBody AddPostRequest addPostRequest) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (addPostRequest == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        boolean result = postService.publishPost(addPostRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "帖子保存数据库失败");
        }
        return ResultUtil.success(true);
    }

    /**
     * 帖子图片上传
     * @param request 会话请求
     * @param file 图片文件
     * @param postId 帖子id
     * @return 图片链接
     */
    @PostMapping("/postImage/upload")
    public BaseResponse<String> uploadImage(HttpServletRequest request, MultipartFile file, Long postId) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "图片没有上传");
        }
        String image = OSSUploadUtil.upload(file, postId, "image");
        return ResultUtil.success(image);
    }

    @PostMapping("/publishComment")
    public BaseResponse<Boolean> publishComment(HttpServletRequest request, @RequestBody AddCommentRequest addCommentRequest) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (addCommentRequest == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        boolean result = postCommentsService.publishComment(addCommentRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(true);
    }
}