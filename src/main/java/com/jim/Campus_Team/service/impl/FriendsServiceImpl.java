package com.jim.Campus_Team.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Campus_Team.entity.domain.Friends;
import com.jim.Campus_Team.service.FriendsService;
import com.jim.Campus_Team.mapper.FriendsMapper;
import org.springframework.stereotype.Service;

/**
* @author Jim_Lam
* @description 针对表【friends(好友表)】的数据库操作Service实现
* @createDate 2024-10-29 21:44:01
*/
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends>
    implements FriendsService{

}




