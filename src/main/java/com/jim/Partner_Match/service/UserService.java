package com.jim.Partner_Match.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jim.Partner_Match.common.BaseResponse;
import com.jim.Partner_Match.entity.domain.User;
import com.jim.Partner_Match.entity.request.UpdateTagRequest;
import com.jim.Partner_Match.entity.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    public List<User> searchUsersByTags(List<String> tagList);
    User getLoginUser(HttpServletRequest request);
    boolean isAdmin(HttpServletRequest request);
    boolean isAdmin(User loginUser);

    int updateUser(User user, User userLogin);

    String setTimeFormat(Date date);

    List<UserVO> recommendUsers(int num, User loginUser);

    boolean modifyTags(UpdateTagRequest tagList, User loginUser);

    boolean uploadAvatar(MultipartFile file, User loginUser);
}
