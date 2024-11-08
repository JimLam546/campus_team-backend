package com.jim.Campus_Team.controller;

import com.jim.Campus_Team.common.BaseResponse;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.common.ResultUtil;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.ChatRequest;
import com.jim.Campus_Team.entity.vo.ChatMessageVO;
import com.jim.Campus_Team.entity.vo.PrivateChatUserVO;
import com.jim.Campus_Team.entity.vo.UserVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.ChatService;
import com.jim.Campus_Team.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.jim.Campus_Team.contant.ChatConstant.PRIVATE_CHAT;
import static com.jim.Campus_Team.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @author Jim_Lam
 * @date 2024-11-05 22:55
 * @description ChatController
 */

@RestController
@RequestMapping("/chat")
@CrossOrigin(originPatterns = {"http://localhost:5173"}, allowCredentials = "true")
public class ChatController {

    @Resource
    private ChatService chatService;

    @Resource
    private UserService userService;

    @RequestMapping("/privateChatList")
    public BaseResponse<List<PrivateChatUserVO>> getPrivateChatList(HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<PrivateChatUserVO> privateChatList = chatService.getPrivateChatList(loginUser);
        return ResultUtil.success(privateChatList);
    }

    /**
     * 获取私聊记录
     * @param chatRequest 聊天请求
     * @param request 会话
     * @return 用户消息VO
     */
    @RequestMapping("/privateChat")
    public BaseResponse<List<ChatMessageVO>> getPrivateChat(@RequestBody ChatRequest chatRequest,
                                                           HttpServletRequest request) {
        if (chatRequest == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<ChatMessageVO> privateChatList = chatService.getPrivateChat(chatRequest, PRIVATE_CHAT, loginUser);
        return ResultUtil.success(privateChatList);
    }
}