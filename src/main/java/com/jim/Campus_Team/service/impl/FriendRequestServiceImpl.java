package com.jim.Campus_Team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.entity.domain.FriendRequest;
import com.jim.Campus_Team.entity.request.AddFriendRequest;
import com.jim.Campus_Team.entity.request.OpsFriendRequest;
import com.jim.Campus_Team.entity.vo.FriendRequestVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.FriendRequestService;
import com.jim.Campus_Team.mapper.FriendRequestMapper;
import org.springframework.stereotype.Service;

/**
* @author Jim_Lam
* @description 针对表【friend_request(好友申请表)】的数据库操作Service实现
* @createDate 2024-10-27 21:46:17
*/
@Service
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest>
    implements FriendRequestService{

    @Override
    public boolean createRequest(AddFriendRequest addFriendRequest, Long id) {
        Long receiveId = addFriendRequest.getReceiveId();
        if (receiveId == null || receiveId < 1 ) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setFromId(id);
        BeanUtil.copyProperties(addFriendRequest, friendRequest);
        return this.save(friendRequest);
    }

    @Override
    public boolean opsFriend(OpsFriendRequest opsFriendRequest, Long id) {
        FriendRequest friendRequest = lambdaQuery()
                .eq(FriendRequest::getReceiveId, id)
                .one();
        BeanUtil.copyProperties(opsFriendRequest, friendRequest);
        return this.updateById(friendRequest);
    }
}




