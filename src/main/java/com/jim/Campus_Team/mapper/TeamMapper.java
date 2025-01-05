package com.jim.Campus_Team.mapper;

import com.jim.Campus_Team.entity.domain.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jim.Campus_Team.entity.pojo.TeamUserPOJO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author Jim_Lam
* @description 针对表【team】的数据库操作Mapper
* @createDate 2024-05-03 22:57:49
* @Entity com.jim.campus_team-backend.entity.Team
*/

@Mapper
public interface TeamMapper extends BaseMapper<Team> {
    List<TeamUserPOJO> getTeamUserList();
}




