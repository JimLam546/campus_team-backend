package com.jim.Campus_Team.mapper;

import com.jim.Campus_Team.entity.domain.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Jim_Lam
* @description 针对表【user_team(用户-队伍关系表)】的数据库操作Mapper
* @createDate 2024-05-03 22:59:59
* @Entity com.jim.campus_team-backend.entity.UserTeam
*/

@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}




