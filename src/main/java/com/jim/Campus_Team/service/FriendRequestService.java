package com.jim.Campus_Team.service;

import com.jim.Campus_Team.entity.domain.FriendRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jim.Campus_Team.entity.request.AddFriendRequest;
import com.jim.Campus_Team.entity.request.OpsFriendRequest;
import com.jim.Campus_Team.entity.vo.FriendRequestVO;

/**
* @author Jim_Lam
* @description 针对表【friend_request(好友申请表)】的数据库操作Service
* @createDate 2024-10-27 21:46:17
*/
public interface FriendRequestService extends IService<FriendRequest> {

    boolean createRequest(AddFriendRequest addFriendRequest, Long id);

    boolean opsFriend(OpsFriendRequest opsFriendRequest, Long id);
}
