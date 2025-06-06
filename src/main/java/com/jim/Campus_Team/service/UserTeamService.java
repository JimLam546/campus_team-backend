package com.jim.Campus_Team.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jim.Campus_Team.entity.domain.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jim.Campus_Team.entity.request.TeamQueryRequest;

/**
* @author Jim_Lam
* @description 针对表【user_team(用户-队伍关系表)】的数据库操作Service
* @createDate 2024-05-03 22:59:59
*/
public interface UserTeamService extends IService<UserTeam> {
    QueryWrapper<UserTeam> getUserTeamQueryWrapper(TeamQueryRequest teamQueryRequest);
}
