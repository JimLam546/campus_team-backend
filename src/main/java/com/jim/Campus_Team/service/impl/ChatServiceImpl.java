package com.jim.Campus_Team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.entity.domain.Chat;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.ChatRequest;
import com.jim.Campus_Team.entity.vo.ChatMessageVO;
import com.jim.Campus_Team.entity.vo.ChatUserVO;
import com.jim.Campus_Team.entity.vo.PrivateChatUserVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.mapper.ChatMapper;
import com.jim.Campus_Team.service.ChatService;
import com.jim.Campus_Team.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.jim.Campus_Team.contant.ChatConstant.PRIVATE_CHAT;

/**
 * @author Jim_Lam
 * @date 2024-11-05 22:58
 * @description ChatServiceImpl
 */
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
        implements ChatService {

    @Resource
    private UserService userService;


    @Override
    public List<ChatMessageVO> getPrivateChat(ChatRequest chatRequest, int chatType, User loginUser) {
        Long toId = chatRequest.getToId();
        if (toId == null || toId < 1) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        Long loginUserId = loginUser.getId();
        List<Chat> chatList = lambdaQuery().select(Chat::getFromId, Chat::getToId, Chat::getChatType, Chat::getCreateTime)
                .eq(Chat::getFromId, loginUserId)
                .eq(Chat::getToId, toId)
                .or()
                .eq(Chat::getFromId, toId)
                .eq(Chat::getToId, loginUserId)
                .list();
        List<ChatMessageVO> chatMessageVOList = chatList.stream().map(chat -> {
            ChatMessageVO chatMessageVO = chatResult(chat.getFromId(), chat.getToId(), chat.getText(), chat.getCreateTime());
            if (loginUserId.equals(chat.getFromId())) {
                chatMessageVO.setMyMessage(true);
            }
            return chatMessageVO;
        }).collect(Collectors.toList());
        // todo 将结果保存到 redis 缓存中
        return chatMessageVOList;
    }

    /**
     * 将用户信息封装传给接收的用户
     * @param userId 发送者id
     * @param text 内容
     * @param chatType 聊天类型
     * @param createTime 创建时间
     * @return 用户消息VO
     */
    @Override
    public ChatMessageVO chatResult(Long userId, String text, Integer chatType, Date createTime) {
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        User fromUser = userService.getById(userId);
        // User toUser = userService.getById(toId);
        chatMessageVO.setFromUser(BeanUtil.copyProperties(fromUser, ChatUserVO.class));
        // chatMessageVO.setToUser(BeanUtil.copyProperties(toUser, ChatUserVO.class));
        chatMessageVO.setChatType(chatType);
        chatMessageVO.setCreateTime(userService.setTimeFormat(createTime));
        return chatMessageVO;
    }

    /**
     * 私人聊天结果处理
     * @param userId 发送者
     * @param toId 接受者
     * @param text 内容
     * @param createTime 创建时间
     * @return 用户聊天消息VO
     */
    @Override
    public ChatMessageVO chatResult(Long userId, Long toId, String text, Date createTime) {
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        User fromUser = userService.getById(userId);
        User toUser = userService.getById(toId);
        chatMessageVO.setFromUser(BeanUtil.copyProperties(fromUser, ChatUserVO.class));
        chatMessageVO.setToUser(BeanUtil.copyProperties(toUser, ChatUserVO.class));
        chatMessageVO.setText(text);
        chatMessageVO.setCreateTime(userService.setTimeFormat(createTime));
        chatMessageVO.setChatType(PRIVATE_CHAT);
        return chatMessageVO;
    }

    /**
     * 获取私聊用户列表
     * @param loginUser 登录用户
     * @return 私聊用户列表VO
     */
    @Override
    public List<PrivateChatUserVO> getPrivateChatList(User loginUser) {
        Long userId = loginUser.getId();
        // 获取我发起或我接收的消息
        List<Chat> chatList = lambdaQuery().select(Chat::getFromId, Chat::getToId)
                .eq(Chat::getChatType, PRIVATE_CHAT)
                .eq(Chat::getFromId, userId)
                .or()
                .eq(Chat::getToId, userId)
                .list();
        HashSet<Long> userIdSet = new HashSet<>();
        for (Chat chat: chatList) {
            if (chat.getFromId().equals(userId)) {
                userIdSet.add(chat.getToId());
            } else if(chat.getToId().equals(userId)) {
                userIdSet.add(chat.getFromId());
            } else {
                userIdSet.add(userId);
            }
        }
        if (userIdSet.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_CHAT_HISTORY);
        }
        List<User> userList = userService.listByIds(userIdSet);
        return userList.stream().map(user -> {
            PrivateChatUserVO privateChatUserVO = new PrivateChatUserVO();
            privateChatUserVO.setId(user.getId());
            privateChatUserVO.setAvatarUrl(user.getAvatarUrl());
            privateChatUserVO.setUsername(user.getUsername());
            Pair<String, Date> privateLastMessage = getPrivateLastMessage(userId, user.getId());
            privateChatUserVO.setLastMessage(privateLastMessage.getKey());
            privateChatUserVO.setLastMessageDate(privateLastMessage.getValue());
            return privateChatUserVO;
        }).collect(Collectors.toList());
    }

    /**
     * 获取私聊最后一条消息
     * @param userId 登录用户id
     * @param toId 目标用户id
     * @return 最后一条消息和时间
     */
    private Pair<String, Date> getPrivateLastMessage(Long userId, Long toId) {
        // 登录用户的最后一条记录
        Chat myLastMessage = this.lambdaQuery()
                .eq(Chat::getFromId, userId).eq(Chat::getToId, toId)
                .orderByDesc(Chat::getCreateTime)
                .last("limit 1").one();
        // 目标用户的最后一条记录
        Chat toLastMessage = this.lambdaQuery()
                .eq(Chat::getFromId, toId).eq(Chat::getToId, userId)
                .orderByDesc(Chat::getCreateTime)
                .last("limit 1")
                .one();
        if (myLastMessage == null && toLastMessage == null) {
            return new Pair<>("", null);
        }
        if (myLastMessage == null) {
            return new Pair<>(toLastMessage.getText(), toLastMessage.getCreateTime());
        }
        if (toLastMessage == null) {
            return new Pair<>(myLastMessage.getText(), myLastMessage.getCreateTime());
        }
        if (myLastMessage.getCreateTime().before(toLastMessage.getCreateTime())) {
            return new Pair<>(toLastMessage.getText(), toLastMessage.getCreateTime());
        }
        return new Pair<>(myLastMessage.getText(), myLastMessage.getCreateTime());
    }
}