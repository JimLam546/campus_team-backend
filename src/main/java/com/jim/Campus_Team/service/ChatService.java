package com.jim.Campus_Team.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jim.Campus_Team.entity.domain.Chat;
import com.jim.Campus_Team.entity.vo.ChatMessageVO;

import java.util.Date;

/**
 * @author Jim_Lam
 * @date 2024-11-05 22:56
 * @description ChatService
 */
public interface ChatService extends IService<Chat> {

    ChatMessageVO chatResult(Long userId, Long toId, String text, Integer chatType, Date createTime);
}