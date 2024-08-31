package com.jim.Partner_Match.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Partner_Match.entity.domain.UserTeam;
import com.jim.Partner_Match.service.UserTeamService;
import com.jim.Partner_Match.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Jim_Lam
* @description 针对表【user_team(用户-队伍关系表)】的数据库操作Service实现
* @createDate 2024-05-03 22:59:59
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




