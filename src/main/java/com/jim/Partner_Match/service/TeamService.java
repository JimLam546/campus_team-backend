package com.jim.Partner_Match.service;

import com.jim.Partner_Match.entity.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jim.Partner_Match.entity.domain.User;
import com.jim.Partner_Match.entity.request.*;
import com.jim.Partner_Match.entity.vo.TeamUserVO;

import java.util.List;

/**
* @author Jim_Lam
* @description 针对表【team】的数据库操作Service
* @createDate 2024-05-03 22:57:49
*/
public interface TeamService extends IService<Team> {

    boolean addTeam(AddTeamRequest teamRequest, User loginUser);

    List<TeamUserVO> listTeams(TeamQueryRequest teamQueryRequest, boolean isAdmin);

    boolean teamUpdate(TeamUpdateRequest teamUpdateRequest, User loginUser);

    boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser);

    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    boolean deleteTeam(long teamId, User loginUser);
}
