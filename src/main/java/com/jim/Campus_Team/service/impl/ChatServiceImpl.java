package com.jim.Campus_Team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Campus_Team.entity.domain.Chat;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.vo.ChatMessageVO;
import com.jim.Campus_Team.entity.vo.ChatUserVO;
import com.jim.Campus_Team.mapper.ChatMapper;
import com.jim.Campus_Team.service.ChatService;
import com.jim.Campus_Team.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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
    public ChatMessageVO chatResult(Long userId, Long toId, String text, Integer chatType, Date createTime) {
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        User fromUser = userService.getById(userId);
        User toUser = userService.getById(toId);
        chatMessageVO.setFromUser(BeanUtil.copyProperties(fromUser, ChatUserVO.class));
        chatMessageVO.setToUser(BeanUtil.copyProperties(toUser, ChatUserVO.class));
        chatMessageVO.setChatType(chatType);
        chatMessageVO.setCreateTime(userService.setTimeFormat(createTime));
        return chatMessageVO;
    }
}