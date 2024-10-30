package com.jim.Campus_Team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.entity.domain.FriendRequest;
import com.jim.Campus_Team.entity.domain.Friends;
import com.jim.Campus_Team.entity.request.AddFriendRequest;
import com.jim.Campus_Team.entity.request.OpsFriendRequest;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.FriendRequestService;
import com.jim.Campus_Team.mapper.FriendRequestMapper;
import com.jim.Campus_Team.service.FriendsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jim_Lam
 * @description 针对表【friend_request(好友申请表)】的数据库操作Service实现
 * @createDate 2024-10-27 21:46:17
 */
@Service
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest>
        implements FriendRequestService {

    /**
     * 请求未操作
     */
    private static final int NOT_OPERATE = 0;
    /**
     * 同意请求
     */
    private static final int ACCEPT = 1;
    /**
     * 拒绝请求
     */
    private static final int REFUSE = 2;
    @Resource
    private FriendsService friendsService;

    @Override
    public boolean createRequest(AddFriendRequest addFriendRequest, Long id) {
        Long receiveId = addFriendRequest.getReceiveId();
        if (receiveId == null || receiveId < 1) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        // 验证添加的好友是不是自己
        if(receiveId.equals(id)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        // 验证对方是否已经是好友
        List<Friends> friendList = friendsService.query().eq("fromId", id).list();
        List<Long> friendIdList = friendList.stream().map(Friends::getFriendId).collect(Collectors.toList());
        if (friendIdList.contains(receiveId)) {
            throw new BusinessException(ErrorCode.IS_FRIEND);
        }
        // 验证是否已经发送过请求（发送方是自己、接收方是对方、请求未操作）
        Long count = this.lambdaQuery().eq(FriendRequest::getFromId, id).eq(FriendRequest::getReceiveId, addFriendRequest.getReceiveId()).eq(FriendRequest::getState, NOT_OPERATE).count();
        if(count > 0) {
            throw new BusinessException(ErrorCode.IS_REQUEST);
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
                .eq(FriendRequest::getFromId, opsFriendRequest.getFromId())
                .eq(FriendRequest::getState, NOT_OPERATE)
                .one();
        // 验证表中的请求是否已经操作过
        if (friendRequest == null) {
            throw new BusinessException(ErrorCode.IS_OPERATE);
        }
        Integer state = opsFriendRequest.getState();
        // 请求同意
        if (state == ACCEPT) {
            // 接收方将发起方加入好友列
            Friends receiveFriend = new Friends();
            receiveFriend.setFromId(id);
            receiveFriend.setFriendId(friendRequest.getFromId());
            // 发起方将接收方加入好友列
            Friends fromFriend = new Friends();
            fromFriend.setFromId(receiveFriend.getFriendId());
            fromFriend.setFriendId(id);
            // 将两条好友记录一起加入表中
            ArrayList<Friends> friendList = new ArrayList<>();
            friendList.add(receiveFriend);
            friendList.add(fromFriend);
            friendsService.saveBatch(friendList);
        }
        // 操作请求记录
        BeanUtil.copyProperties(opsFriendRequest, friendRequest);
        return this.updateById(friendRequest);
    }
}




