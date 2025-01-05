package com.jim.Campus_Team.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jim.Campus_Team.common.BaseResponse;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.UpdateTagRequest;
import com.jim.Campus_Team.entity.request.UserQueryRequest;
import com.jim.Campus_Team.entity.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
* @author Jim_Lam
* @description 针对表【user】的数据库操作Service
* @createDate 2024-03-27 20:50:26
*/
public interface UserService extends IService<User> {
    BaseResponse<Long> userRegister(String userAccount, String username, String userPassword, String checkPassword);
    public BaseResponse<User> doLogin(String userAccount, String userPassword, HttpServletRequest request);
    public void userLough(HttpServletRequest request);
    public List<User> searchUsersByTags(long pageNum, long pageSize, List<String> tagList);
    User getLoginUser(HttpServletRequest request);
    boolean isAdmin(HttpServletRequest request);
    boolean isAdmin(User loginUser);

    int updateUser(User user, User userLogin);

    String setTimeFormat(Date date);

    List<UserVO> recommendUsers(int num, User loginUser);

    boolean modifyTags(UpdateTagRequest tagList, User loginUser);
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    String uploadAvatar(MultipartFile file, User loginUser, String avatarType);

    List<UserVO> getUserVOList(List<User> records);
}
