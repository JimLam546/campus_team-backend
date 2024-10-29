package com.jim.Campus_Team.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jim.Campus_Team.common.BaseResponse;
import com.jim.Campus_Team.common.ErrorCode;
import com.jim.Campus_Team.common.ResultUtil;
import com.jim.Campus_Team.entity.domain.Friends;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.domain.UserId;
import com.jim.Campus_Team.entity.request.PageRequest;
import com.jim.Campus_Team.entity.request.UpdateTagRequest;
import com.jim.Campus_Team.entity.request.UserLoginRequest;
import com.jim.Campus_Team.entity.request.UserRegisterRequest;
import com.jim.Campus_Team.entity.vo.UserVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.FriendsService;
import com.jim.Campus_Team.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jim.Campus_Team.common.ErrorCode.*;
import static com.jim.Campus_Team.contant.UserConstant.USER_LOGIN_STATE;

@CrossOrigin(origins = {"http://47.115.163.154:5173", "http://47.115.163.154:80", "http://localhost:5173"}, allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private FriendsService friendsService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null)
            return ResultUtil.error(NULL_ERROR);
        // throw new BusinessException(ErrorCode.NULL_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String username = userRegisterRequest.getUsername();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, username, userPassword, checkPassword))
            return ResultUtil.error(PARAMETER_ERROR, "参数不能为空");
        return userService.userRegister(userAccount, username, userPassword, checkPassword);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null)
            return null;
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.doLogin(userAccount, userPassword, request);
    }

    @GetMapping("/query")
    public BaseResponse<List<UserVO>> query(String username, PageRequest pageRequest, HttpServletRequest request) {
        System.out.println(username);
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null)
            throw new BusinessException(NOT_LOGIN);
        Page<User> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize(), false);
        List<User> userList = userService.query().like("username", username).list(page);
        List<UserVO> userVOList = userList.stream()
                .map(user -> BeanUtil.copyProperties(user, UserVO.class))
                .collect(Collectors.toList());
        System.out.println(username);
        return ResultUtil.success(userVOList);
    }

    @GetMapping("/search")
    public List<User> searchUsers(String username, String userAccount, HttpServletRequest request) {
        // 只有管理员可以查询用户
        if (!userService.isAdmin(request))
            return new ArrayList<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(username), "username", username).or().like(StringUtils.isNotEmpty(userAccount), "userAccount", userAccount);
        return userService.list(queryWrapper);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody UserId id, HttpServletRequest request) {
        if (!userService.isAdmin(request))
            return ResultUtil.error(ErrorCode.NO_AUTO);
        if (id.getId() <= 0)
            return ResultUtil.error(ErrorCode.PARAMETER_ERROR);
        if (((User) request.getSession().getAttribute(USER_LOGIN_STATE)).getId() == id.getId())
            return ResultUtil.error(5000, "", "不能删除自己");
        return ResultUtil.success(userService.removeById(id.getId()));
    }

    @PostMapping("/lough")
    public void userLough(HttpServletRequest request) {
        userService.userLough(request);
    }

    // 获取当前用户信息
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        long id = 0;
        try {
            id = ((User) request.getSession().getAttribute(USER_LOGIN_STATE)).getId();
        } catch (Exception e) {
            throw new BusinessException(NOT_LOGIN, "身份信息异常，请重新登录");
        }
        User user = userService.getById(id);
        if (user == null)
            return ResultUtil.error(NOT_LOGIN);
        User safeUser = new User();
        BeanUtils.copyProperties(user, safeUser);
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        safeUser.setCreateTimeStr(timeFormat.format(user.getCreateTime()));
        safeUser.setUserPassword(null);
        safeUser.setIsDelete(null);
        return new BaseResponse<>(0, safeUser);
    }

    // // 获取系统所有用户信息
    // @GetMapping("/list")
    // public List<User> list(HttpServletRequest request) {
    //     // 管理员才可以查看所有用户信息
    //     if(request.getSession().getAttribute(USER_LOGIN_STATE) == null || !isAdmin(request))
    //         return new ArrayList<>();
    //     return userService.list();
    // }

    // 分页查询
    @GetMapping("/pageList")
    public BaseResponse<List<User>> list(PageRequest pageRequest, HttpServletRequest request) {
        // 管理员才能查看用户信息
        if (pageRequest.getPageNum() <= 0 || pageRequest.getPageSize() <= 0)
            return ResultUtil.error(ErrorCode.PARAMETER_ERROR);
        try {
            request.getSession().getAttribute(USER_LOGIN_STATE);
        } catch (Exception e) {
            throw new BusinessException(NOT_LOGIN, "身份信息异常，请重新登录");
        }
        // 判断是不是管理员
        if (!userService.isAdmin(request))
            return ResultUtil.error(ErrorCode.NO_AUTO);
        Page<User> page = userService.page(new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize()));
        // 将页内所有元素的时间变成格式时间
        List<User> records = page.getRecords();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (User u : records) {
            u.setCreateTimeStr(dateFormat.format(u.getCreateTime()));
        }
        List<User> userList = page.getRecords();
        return ResultUtil.success(userList);
    }

    // 修改个人信息(用户中心)
    @PutMapping("/modifyInfo")
    public BaseResponse<User> modifyInfo(@RequestBody User userInfo, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userInfo == null)
            return ResultUtil.error(NULL_ERROR);
        // 表示可能 session 失效了
        if (user == null)
            new BaseResponse<>(50000, "身份信息异常，请重新登录");
        if (StringUtils.isAnyBlank(userInfo.getEmail(), userInfo.getPhone(), userInfo.getUsername()))
            return ResultUtil.error(ErrorCode.PARAMETER_ERROR);

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", user.getId());
        updateWrapper.set("username", userInfo.getUsername());
        updateWrapper.set("phone", userInfo.getPhone());
        updateWrapper.set("email", userInfo.getEmail());
        boolean isUpdate = userService.update(updateWrapper);
        if (isUpdate)
            return new BaseResponse<>(0, null);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
    }

    @GetMapping("/search/list")
    public BaseResponse<List<User>> searchUserByTags(long pageNum, long pageSize, @RequestParam(required = false) List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList))
            throw new BusinessException(NULL_ERROR);
        List<User> userList = userService.searchUsersByTags(pageNum, pageSize, tagList);
        return new BaseResponse<>(0, userList);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 1.检查前端是否传递了对象
        if (user == null)
            throw new BusinessException(PARAMETER_ERROR);
        // 2.检查是否是用户自己或管理员修改信息
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null || loginUser.getUserRole() != 1)
            throw new BusinessException(NO_AUTO);
        int i = userService.updateUser(user, loginUser);
        return ResultUtil.success(i); // 更新后没有改session的值
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${RedisKey.temp_Id}")
    private String temp_Id;

    @GetMapping("/recommend")
    public BaseResponse<List<UserVO>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("user:recommend:%s", temp_Id);
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        List<User> userPage = null;
        userPage = JSONUtil.toList(valueOperations.get(redisKey), User.class);
        if (!userPage.isEmpty()) {
            List<UserVO> userVOList = userPage.stream().map(
                            user -> BeanUtil.copyProperties(user, UserVO.class))
                    .collect(Collectors.toList());
            return ResultUtil.success(userVOList);
        }
        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper).getRecords();
        // 写缓存
        try {
            Object o = valueOperations.get(redisKey);
            if (o == null) {
                valueOperations.set(redisKey, JSONUtil.toJsonStr(userPage), 24, TimeUnit.HOURS);
            }
        } catch (Exception e) {
            System.out.println(new Date() + "：" + e.getMessage());
        }
        List<UserVO> userVOList = userPage.stream().map(
                        user -> BeanUtil.copyProperties(user, UserVO.class))
                .collect(Collectors.toList());
        return ResultUtil.success(userVOList);
    }

    // 匹配模式(match)
    @GetMapping("/match")
    public BaseResponse<List<UserVO>> recommend(int num, HttpServletRequest request) {
        if (num <= 0 || num > 20)
            throw new BusinessException(PARAMETER_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null)
            throw new BusinessException(NOT_LOGIN);
        List<UserVO> userVOList = userService.recommendUsers(num, loginUser);
        return ResultUtil.success(userVOList);
    }

    @PostMapping("/modifyTags")
    public BaseResponse<Boolean> modifyTags(@RequestBody UpdateTagRequest updateTagRequest, HttpServletRequest request) {
        if (updateTagRequest == null)
            throw new BusinessException(PARAMETER_ERROR);
        if (CollectionUtils.isEmpty(updateTagRequest.getTagList()))
            throw new BusinessException(NULL_ERROR);
        User loginUser = userService.getLoginUser(request);
        boolean result = userService.modifyTags(updateTagRequest, loginUser);
        if (!result)
            throw new BusinessException(SYSTEM_ERROR);
        return ResultUtil.success(true);
    }

    @PostMapping("/uploadAvatar")
    public BaseResponse<Boolean> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new BusinessException(PARAMETER_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 验证是否登录
        if (loginUser == null) {
            throw new BusinessException(NOT_LOGIN);
        }

        String uploadURL = userService.uploadAvatar(file, loginUser, "userAvatar");
        boolean update = userService.update().set("avatarUrl", uploadURL).eq("id", loginUser.getId()).update();
        if (!update) {
            throw new BusinessException(SYSTEM_ERROR, "图片保存失败");
        }
        return ResultUtil.success(true);
    }

    @GetMapping("/query/{id}")
    public BaseResponse<UserVO> query(HttpServletRequest request, @PathVariable("id") long id) {
        User loginUser =(User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null)
            throw new BusinessException(NOT_LOGIN);
        if (id < 1)
            throw new BusinessException(PARAMETER_ERROR, "用户不存在");
        User user = userService.getById(id);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        // 验证是不是登录用户的好友
        Friends friend = friendsService.lambdaQuery()
                .eq(Friends::getFriendId, id)
                .eq(Friends::getFromId, loginUser.getId()).one();
        if(friend != null) {
            userVO.setFriend(true);
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        userVO.setCreateTimeStr(timeFormat.format(user.getCreateTime()));
        return ResultUtil.success(userVO);
    }

    // @PostMapping("/avatar")
    // public BaseResponse<OutputStream> avatar(@RequestBody UserId userId, HttpServletResponse response) {
    //     User loginUser = userService.getLoginUser(request);
    //
    // }
}
