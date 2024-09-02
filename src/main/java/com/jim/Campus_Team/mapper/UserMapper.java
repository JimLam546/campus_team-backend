package com.jim.Campus_Team.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jim.Campus_Team.entity.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Jim_Lam
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-03-27 20:50:26
* @Entity com.jim.campus_team-backend.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {
}




