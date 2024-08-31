package com.jim.Partner_Match.mapper;

import com.jim.Partner_Match.entity.domain.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Jim_Lam
* @description 针对表【team】的数据库操作Mapper
* @createDate 2024-05-03 22:57:49
* @Entity com.jim.Match_Team.entity.Team
*/

@Mapper
public interface TeamMapper extends BaseMapper<Team> {

}




