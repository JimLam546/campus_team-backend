package com.jim.Campus_Team.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TeamUserVO implements Serializable {

    private static final long serialVersionUID = -3183194489313379064L;


    /**
     * 关联表记录id(当队伍id用)
     */
    private Long id;


    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 创建队伍时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 过期时间
     */
    private String expireTime;

    /**
     * 队伍最大人数
     */
    private int maxNum;

    /**
     * 已加入队伍人数
     */
    private int hasJoinNum;

    /**
     * 队长信息
     */
    private UserVO createUser;

    /**
     * 队伍状态
     */
    private int teamStatus;

    /**
     * 是否该了加入队伍
     */
    private boolean hasJoin;

    /**
     * 该队用户
     */
    private List<UserVO> teamUserList;

    /**
     * 队伍头像
     */
    private String avatarUrl;
}
