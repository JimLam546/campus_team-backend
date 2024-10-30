package com.jim.Campus_Team.controller;

import cn.hutool.core.bean.BeanUtil;
import com.jim.Campus_Team.common.BaseResponse;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.common.ResultUtil;
import com.jim.Campus_Team.entity.domain.FriendRequest;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.AddFriendRequest;
import com.jim.Campus_Team.entity.request.OpsFriendRequest;
import com.jim.Campus_Team.entity.vo.FriendRequestVO;
import com.jim.Campus_Team.entity.vo.UserVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.FriendRequestService;
import com.jim.Campus_Team.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Collectors;

import static com.jim.Campus_Team.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @author Jim_Lam
 * @date 2024-10-27 21:35
 * @description RequestController
 */
@CrossOrigin(value = {"http://localhost:5173"}, allowCredentials = "true")
@RestController
@RequestMapping("/request")
public class RequestController {

    @Resource
    private FriendRequestService friendRequestService;

    @Resource
    private UserService userService;

    /**
     * 创建好友请求
     * @param addFriendRequest
     * @param request
     * @return
     */
    @PostMapping("/addFriend")
    public BaseResponse<Boolean> createRequest(@RequestBody AddFriendRequest addFriendRequest,
                                               HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long receiveId = addFriendRequest.getReceiveId();
        if (receiveId == null || receiveId < 1) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        boolean isCreate = friendRequestService.createRequest(addFriendRequest, loginUser.getId());
        if (!isCreate) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(true);
    }

    /**
     * 操作好友请求
     *
     * @param opsFriendRequest
     * @param request
     * @return
     */
    @PostMapping("/opsFriend")
    public BaseResponse<Boolean> opsFriend(@RequestBody OpsFriendRequest opsFriendRequest,
                                           HttpServletRequest request) {
        // 登录用户为请求接收者
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean result = friendRequestService.opsFriend(opsFriendRequest, loginUser.getId());
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(true);
    }

    /**
     * 获取申请列表
     */
    @GetMapping("/getRequestList")
    public List<FriendRequestVO> getRequestList(HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = loginUser.getId();
        List<FriendRequest> friendRequestList = friendRequestService.lambdaQuery()
                .eq(FriendRequest::getReceiveId, loginUser.getId())
                .list();
        return friendRequestList.stream()
                .map(friendRequest -> {
                    FriendRequestVO friendRequestVO = new FriendRequestVO();
                    BeanUtil.copyProperties(friendRequest, friendRequestVO);
                    // 修改时间格式
                    friendRequestVO.setCreateTime(userService.setTimeFormat(friendRequest.getCreateTime()));
                    // 获取用户实例
                    if (!friendRequest.getFromId().equals(userId)) {
                        friendRequestVO.setFromUserVO(
                                BeanUtil.copyProperties(
                                        userService.getById(friendRequest.getFromId()), UserVO.class));
                    }
                    return friendRequestVO;
                })
                .collect(Collectors.toList());
    }
}