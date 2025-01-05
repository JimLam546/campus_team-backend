package com.jim.Campus_Team.entity.pojo;

/**
 * @author Jim_Lam
 * @description TeamUserPOJO
 */

import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.vo.UserVO;
import lombok.Data;

import java.util.List;

/**
 * 队伍与用户的一对多映射类
 */
@Data
public class TeamUserPOJO {

    /**
     * 队伍ID
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
     * 队长用户ID
     */
    private Long userId;

    /**
     * 创建队伍时间
     */
    private String createTime;

    /**
     * 过期时间
     */
    private String expireTime;

    /**
     * 队伍最大人数
     */
    private int maxNum;

    /**
     * 队伍状态
     */
    private int teamStatus;

    /**
     * 该队用户
     */
    private List<Long> userIdList;

    /**
     * 队伍头像
     */
    private String avatarUrl;
}