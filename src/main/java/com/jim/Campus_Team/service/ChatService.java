package com.jim.Campus_Team.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jim.Campus_Team.entity.domain.Chat;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.ChatRequest;
import com.jim.Campus_Team.entity.vo.ChatMessageVO;
import com.jim.Campus_Team.entity.vo.PrivateChatUserVO;
import com.jim.Campus_Team.entity.vo.TeamChatVO;

import java.util.Date;
import java.util.List;

/**
 * @author Jim_Lam
 * @date 2024-11-05 22:56
 * @description ChatService
 */
public interface ChatService extends IService<Chat> {

    List<ChatMessageVO> getPrivateChat(ChatRequest chatRequest, int chatType, User loginUser);

    ChatMessageVO chatResult(Long userId, String text, Integer chatType, Date createTime);

    ChatMessageVO chatResult(Long userId, Long toId, String text, Date createTime);

    List<PrivateChatUserVO> getPrivateChatList(User loginUser);

    List<TeamChatVO> getTeamChatList(User loginUser);

    List<ChatMessageVO> getTeamChat(User loginUser, Long teamId);
}