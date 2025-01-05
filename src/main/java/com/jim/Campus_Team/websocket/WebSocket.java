package com.jim.Campus_Team.websocket;

import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.jim.Campus_Team.config.HttpSessionConfig;
import com.jim.Campus_Team.entity.domain.Chat;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.domain.UserTeam;
import com.jim.Campus_Team.entity.request.MessageRequest;
import com.jim.Campus_Team.entity.vo.ChatMessageVO;
import com.jim.Campus_Team.service.ChatService;
import com.jim.Campus_Team.service.TeamService;
import com.jim.Campus_Team.service.UserService;
import com.jim.Campus_Team.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.jim.Campus_Team.contant.ChatConstant.*;
import static com.jim.Campus_Team.contant.UserConstant.*;

/**
 * @author Jim_Lam
 * @date 2024-10-31 22:46
 * @description WebSocket
 */

@Slf4j
@Component
@ServerEndpoint(value = "/chat/{userId}/{teamId}", configurator = HttpSessionConfig.class)
public class WebSocket {

    // userId是登录用户id

    /**
     * <队伍id：<用户id：webSocket>>
     */
    private static final Map<String, ConcurrentHashMap<String, WebSocket>> ROOMS = new HashMap<>();

    private static final Map<String, Session> SESSION_POOL = new HashMap<>();

    /**
     * Http 会话 Session
     */
    private  HttpSession httpSession;

    /**
     * WebSocket 会话 Session
     */
    private Session session;

    private static TeamService teamService;

    private static UserService userService;

    private static UserTeamService userTeamService;

    private static ChatService chatService;

    @Resource
    public void setUserService(UserService userService) {
        WebSocket.userService = userService;
    }

    @Resource
    public void setTeamService(TeamService teamService) {
        WebSocket.teamService = teamService;
    }

    @Resource
    public void setUserTeamService(UserTeamService userTeamService) {
        WebSocket.userTeamService = userTeamService;
    }

    @Resource
    public void setChatService(ChatService chatService) {
        WebSocket.chatService = chatService;
    }

    /**
     * @param session WebSocket 会话
     * @param userId  请求连接的用户Id
     * @param teamId  队伍Id
     * @param config  配置
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam(value = "userId") String userId,
                       @PathParam(value = "teamId") String teamId,
                       EndpointConfig config) {
        if (StringUtils.isBlank(userId) || "undefined".equals(userId)) {
            log.error("空userId路径参数，尝试WebSocket连接");
        }
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        User loginUser = (User) httpSession.getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            sendError(userId, "你未登录");
        }
        this.session = session;
        this.httpSession = httpSession;
        // 群聊
        if (!"NaN".equals(teamId)) {
            // 用户用户信息，看是不是管理员
            User user = userService.lambdaQuery().eq(User::getId, userId).one();
            if (user == null) return;
            // 如果是用户角色则判断在不在队伍内
            if (user.getUserRole().equals(USER_ROLE)) {
                // 验证用户在不在队伍内
                UserTeam one = userTeamService.lambdaQuery()
                        .select(UserTeam::getUserId)
                        .eq(UserTeam::getTeamId, Long.parseLong(teamId))
                        .eq(UserTeam::getUserId, Long.parseLong(userId))
                        .one();
                // 用户不在队伍内
                if (one == null) {
                    sendError(userId, "你不是该队伍的成员");
                    return;
                }
            }
            // 查看群聊是否已经创建，没有则创建（双重验证）
            if (!ROOMS.containsKey(teamId)) {
                synchronized (teamId.intern()) {
                    if (!ROOMS.containsKey(teamId)) {
                        ROOMS.put(teamId, new ConcurrentHashMap<>());
                    }
                }
            }
            // 检查自己是否在 ROOMS 的群聊用户里存在
            ConcurrentHashMap<String, WebSocket> inTeamUserWebSocketMap = ROOMS.get(teamId);
            // 队伍群聊用户里，自己还未上线，则将该 websocket 添加进入
            if (inTeamUserWebSocketMap.get(userId) == null) {
                inTeamUserWebSocketMap.put(userId, this);
            } else {
                // 如果已存在，则关闭原来的 websocket
                try {
                    inTeamUserWebSocketMap.get(userId).session.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    inTeamUserWebSocketMap.put(userId, this);
                }
            }
        } else {
            // 私聊
            if (SESSION_POOL.get(userId) == null) {
                SESSION_POOL.put(userId, session);
            } else {
                try {
                    SESSION_POOL.get(userId).close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    SESSION_POOL.put(userId, session);
                }
            }
        }
        log.info("当前连接服务器用户：" + SESSION_POOL.size() + "人");
        log.info("当前已建立的群聊：" + ROOMS.size() + "个");
    }

    /**
     *
     * @param message
     * @param userId 请求连接的用户 id
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        // 保持心跳
        if (message.equals("PING")) {
            sendOneMessage(userId, "PONG");
            return;
        }
        // log.info("服务端接收到用户 " + userId + " 发送的: " + message);
        MessageRequest messageRequest = new Gson().fromJson(message, MessageRequest.class);
        Long toId = messageRequest.getToId();
        Long teamId = messageRequest.getTeamId();
        String text = messageRequest.getText();
        Integer chatType = messageRequest.getChatType();
        if (PRIVATE_CHAT.equals(chatType)) {
            // 私聊
            // log.info("消息:" + text);
            private_chat(Long.parseLong(userId), toId, text, chatType);
        } else if (TEAM_CHAT.equals(chatType)) {
            // 群聊
            team_chat(Long.parseLong(userId), teamId, text, chatType);
        } else if (SYSTEM_CHAT.equals(chatType)) {
            // 管理员发消息
            team_chat(Long.parseLong(userId), teamId, text, chatType);
        }
    }


    @OnClose
    public void onClose(@PathParam("userId") String userId, @PathParam(value = "teamId") String teamId, Session session) {
        if ("NaN".equals(teamId)) {
            if (!SESSION_POOL.isEmpty()) {
                SESSION_POOL.remove(userId);
            }
        } else {
            ROOMS.get(teamId).remove(userId);
            log.info("userId：" + userId + "，断开连接");
        }
    }

    /**
     * 发送一条错误消息
     *
     * @param userId       用户id
     * @param errorMessage 错误消息
     */
    private void sendError(String userId, String errorMessage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("error", errorMessage);
        sendOneMessage(userId, jsonObject.toString());
    }

    /**
     * 发送一条消息
     * @param userId 接收用户id
     * @param message 消息
     */
    public void sendOneMessage(String userId, String message) {
        Session wsSession = SESSION_POOL.get(userId);
        if (wsSession != null && wsSession.isOpen()) {
            synchronized (userId.intern()) {
                try {
                    wsSession.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 私发消息
     * @param userId 用户id
     * @param toId 对方用户id
     * @param text 内容
     * @param chatType 聊天类型
     */
    public void private_chat(Long userId, Long toId, String text, Integer chatType) {
        ChatMessageVO chatMessageVO = chatService.chatResult(userId, text, chatType, new Date());
        User loginUser = (User) httpSession.getAttribute(USER_LOGIN_STATE);
        if(loginUser.getId().equals(toId)) {
            chatMessageVO.setMyMessage(true);
        }
        String json = new Gson().toJson(chatMessageVO);
        // 发送消息
        sendOneMessage(toId.toString(), json);
        // 消息保存
        boolean result = saveChat(userId, toId, null, text, chatType);
        if (!result) {
            log.error("userId=" + userId + ",发送私聊消息失败：" + text);
        }
        // todo 将缓存中的用户消息删除（保证数据一致性）
    }

    /**
     * 群发消息
     * @param userId 发送者
     * @param teamId 队伍id
     * @param text 内容
     * @param chatType 聊天类型
     */
    private void team_chat(Long userId, Long teamId, String text, Integer chatType) {
        ChatMessageVO chatMessageVO = chatService.chatResult(userId, text, chatType, new Date());
        String json = new Gson().toJson(chatMessageVO);
        synchronized (teamId.toString().intern()) {
            broadcastMessage(teamId, json);
        }
        boolean result = saveChat(userId, null, teamId, text, chatType);
        if(!result) {
            log.error("userId=" + userId + ",teamId=" + teamId + ",发送群聊消息存储失败：" + text);
        }
    }

    /**
     * 广播消息
     * @param teamId 队伍id
     * @param text 聊天内容
     */
    private void broadcastMessage(Long teamId, String text) {
        ConcurrentHashMap<String, WebSocket> inTeamUserWebSocket = ROOMS.get(teamId.toString());
        for (WebSocket webSocket : inTeamUserWebSocket.values()) {
            if (webSocket == this) {
                continue;
            }
            try {
                webSocket.session.getBasicRemote().sendText(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean saveChat(Long fromId, Long toId, Long teamId, String text, Integer chatType) {
        Chat chat = new Chat();
        chat.setFromId(fromId);
        if (toId != null && toId > 0) {
            chat.setToId(toId);
        }
        if(teamId != null && teamId > 0) {
            chat.setTeamId(teamId);
        }
        chat.setText(text);
        chat.setChatType(chatType);
        return chatService.save(chat);
    }
}

