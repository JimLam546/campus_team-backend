package com.jim.Campus_Team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jim.Campus_Team.common.AlgorithmUtil;
import com.jim.Campus_Team.common.BaseResponse;
import com.jim.Campus_Team.common.OSSUploadUtil;
import com.jim.Campus_Team.common.ResultUtil;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.request.UpdateTagRequest;
import com.jim.Campus_Team.entity.vo.UserVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.mapper.UserMapper;
import com.jim.Campus_Team.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jim.Campus_Team.common.ErrorCode.*;
import static com.jim.Campus_Team.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @author Jim_Lam
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-03-27 20:50:26
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    //    加密盐
    private static final String SALT = "hello";


    @Override
    public BaseResponse<Long> userRegister(String userAccount, String username, String userPassword, String checkPassword) {
//        校验
        if (StringUtils.isAnyBlank(userAccount, username, userPassword, checkPassword)) {
            return ResultUtil.error(NULL_ERROR);
        }
        if (userAccount.length() < 4 || userAccount.length() > 12)
            return ResultUtil.error(PARAMETER_ERROR);
        if (userPassword.length() < 8)
            return ResultUtil.error(PARAMETER_ERROR);
//        账户不能包含特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find())
            return ResultUtil.error(PARAMETER_ERROR, "账户不能包含特殊字符");
        if (!userPassword.equals(checkPassword))
            return ResultUtil.error(PARAMETER_ERROR, "账号密码与密码不匹配");
//        账户信息不能相同
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0)
            return ResultUtil.error(PARAMETER_ERROR, "该账号已存在");
//        加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUsername(username);
        // user.setUserPassword(md5);
        user.setUserPassword(encryptPassword);
        boolean save = this.save(user);
        if (!save)
            return new BaseResponse<>(40000, "用户信息存入数据库失败", (long) -1);
        return ResultUtil.success((long) 0);
    }


    public BaseResponse<User> doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtil.error(NULL_ERROR);
        }
        if (userAccount.length() < 4)
            return ResultUtil.error(PARAMETER_ERROR);
        if (userPassword.length() < 8)
            return ResultUtil.error(PARAMETER_ERROR);
//        账户不能包含特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        // Pattern p = Pattern.compile(regEx);
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find())
            return ResultUtil.error(PARAMETER_ERROR);
//        加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
//        用户不存在
        if (user == null) {
            log.info("用户登录失败, 账号密码不匹配!");
            return ResultUtil.error(PARAMETER_ERROR);
        }
        // 用户信息脱敏
        User safeUser = getSafeUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);
        return new BaseResponse<>(0, safeUser);
    }

    private User getSafeUser(User user) {
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setIsVaild(user.getIsVaild());
        safeUser.setCreateTime(new Date());
        safeUser.setUpdateTime(new Date());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setTags(user.getTags());
        safeUser.setProfile(user.getProfile());
        return safeUser;
    }

    // 用户注销
    public void userLough(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
    }

    @Override
    public List<User> searchUsersByTags(long pageNum, long pageSize, List<String> tagList) {
        if (tagList.isEmpty()) {
            throw new BusinessException(PARAMETER_ERROR);
        }
        // 通过内容查询标签用户
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        for (String tag : tagList) {
            userLambdaQueryWrapper = userLambdaQueryWrapper.or().like(StringUtils.isNotEmpty(tag), User::getTags, tag);
        }
        return page(new Page<>(pageNum, pageSize), userLambdaQueryWrapper).getRecords();

        // // 全部取出过滤指定标签的方式
        // QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // List<User> userList = userMapper.selectList(queryWrapper);
        // return userList.stream().filter(user -> {
        //     Gson gson = new Gson();
        //     Set<String> set = gson.fromJson(user.getTags(), new TypeToken<Set<String>>() {
        //     }.getType());
        //     set = Optional.ofNullable(set).orElse(new HashSet<>());
        //     for (String tag : tagList) {
        //         if (!set.contains(tag))
        //             return false;
        //     }
        //     return true;
        // }).map(this::getSafeUser).collect(Collectors.toList());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null)
            throw new BusinessException(NOT_LOGIN);
        Object user = request.getSession().getAttribute(USER_LOGIN_STATE);
        return (User) user;
    }

    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getUserRole() == 1;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == 1;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        // 1.检查用户是否传递了是否成功
        if (user == null)
            throw new BusinessException(PARAMETER_ERROR);
        // 2.检查是否是用户自己或管理员修改信息
        if (loginUser.getUserRole() != 1 && !user.getId().equals(loginUser.getId()))
            throw new BusinessException(NO_AUTO);
        User oldUser = userMapper.selectById(loginUser.getId());
        if (oldUser == null)
            throw new BusinessException(NOT_LOGIN);
        user.setId(loginUser.getId());
        return userMapper.updateById(user);
    }

    @Override
    public String setTimeFormat(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timeFormat.format(date);
    }

    @Override
    public List<UserVO> recommendUsers(int num, User loginUser) {
        if (num <= 0 || num >= 20)
            throw new BusinessException(PARAMETER_ERROR);
        if (loginUser == null)
            throw new BusinessException(NOT_LOGIN);
        long userId = loginUser.getId();
        User user = this.getById(userId);
        String tags = user.getTags();
        Gson gson = new Gson();
        // 将登录用户的标签转换为list
        ArrayList<String> tagList = gson.fromJson(tags, new TypeToken<ArrayList<String>>() {
        }.getType());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        List<User> userIdTagList = this.list(queryWrapper);
        // 用户id - 编辑距离
        ArrayList<Pair<Long, Long>> list = new ArrayList<>();
        for (User userIdTag : userIdTagList) {
            if (StringUtils.isBlank(userIdTag.getTags()) || userIdTag.getId() == userId) {
                continue;
            }
            List<String> userTags = gson.fromJson(userIdTag.getTags(), new TypeToken<ArrayList<String>>() {
            }.getType());
            long minDistance = AlgorithmUtil.minDistance(tagList, userTags);
            list.add(new Pair<>(userIdTag.getId(), minDistance));
        }
        // 将所有的用户编辑距离排序（升序）
        List<Pair<Long, Long>> pairList = list.stream().
                sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        QueryWrapper<User> resultQuery = new QueryWrapper<>();
        List<Long> idList = pairList.stream().map(Pair::getKey).collect(Collectors.toList());
        if (idList.size() == 0) {
            return Collections.emptyList();
        }
        String ids = StringUtils.join(idList, ',');
        resultQuery.in("id", idList);
        resultQuery.last("ORDER BY FIELD( 'id', " + ids + ")");
        List<User> resList = this.list(resultQuery);
        if (CollectionUtils.isEmpty(resList)) {
            return new ArrayList<>();
        }
        return resList.stream().map(u -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(u, userVO);
            return userVO;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean modifyTags(@RequestBody UpdateTagRequest updateTagRequest, User loginUser) {
        if (updateTagRequest == null)
            throw new BusinessException(PARAMETER_ERROR);
        if (CollectionUtils.isEmpty(updateTagRequest.getTagList()))
            throw new BusinessException(NULL_ERROR);
        Gson gson = new Gson();
        String tags = gson.toJson(updateTagRequest.getTagList());
        boolean isUpdate = this.update().eq("id", loginUser.getId()).set("tags", tags).update();
        if (!isUpdate)
            throw new BusinessException(SYSTEM_ERROR, "更新数据库失败");
        return true;
    }

    /**
     * 用户头像上传
     *
     * @param avatar
     * @param loginUser
     * @return
     */
    public String uploadAvatar(MultipartFile avatar, User loginUser, String avatarType) {
        if (loginUser == null)
            throw new BusinessException(NOT_LOGIN);
        if (avatar.isEmpty())
            throw new BusinessException(PARAMETER_ERROR);
        // 获取上传文件的文件类型
        String fileType = avatar.getContentType();
        // System.out.println("文件类型=" + fileType);

        // 保证上传的文件类型是 png 或 jpg
        if (!("image/png".equals(fileType) || "image/jpeg".equals(fileType))) {
            throw new BusinessException(PARAMETER_ERROR, "上传的文件类型必须是 png 或 jpg 格式");
        }

        // 第三个参数表示上传的图片是队伍的还是用户的
        return OSSUploadUtil.upload(avatar, loginUser.getId(), avatarType);

    }

    // 上传本地
    // @Override
    // public boolean uploadAvatar(MultipartFile avatar, User loginUser) {
    //     if (loginUser == null)
    //         throw new BusinessException(NOT_LOGIN);
    //     if(avatar.isEmpty())
    //         throw new BusinessException(PARAMETER_ERROR);
    //     // 获取上传文件的文件类型
    //     String fileType = avatar.getContentType();
    //     // System.out.println("文件类型=" + fileType);
    //
    //     // 保证上传的文件类型是 png 或 jpg
    //     if(!("image/png".equals(fileType) || "image/jpeg".equals(fileType))) {
    //         throw new BusinessException(PARAMETER_ERROR, "上传的文件类型必须是 png 或 jpg 格式");
    //     }
    //
    //     String path = Objects.requireNonNull(getClass().getResource("/")).getPath();
    //     String projectPath = URLDecoder.decode(path, "UTF-8");
    //     System.out.println("资源路径=" + projectPath);
    //
    //     System.out.println("项目路径" + projectPath);
    //     String avatarPath = projectPath + "static/userAvatar";
    //     File avatarPathFile = new File(avatarPath);
    //     // 判断是否存在 userAvatar 存放用户头像的文件夹
    //     if(!avatarPathFile.exists()) {
    //         avatarPathFile.mkdirs();
    //     }
    //     // 判断当前月的文件夹是否被创建
    //     Date date = new Date();
    //     SimpleDateFormat format = new SimpleDateFormat("YYYY-MM");
    //     // System.out.println(format.format(date));
    //     String yearMonth = format.format(date);
    //     String avatarDatePath = avatarPath + "/" + yearMonth;
    //     File avatarDateFile = new File(avatarDatePath);
    //     if(!avatarDateFile.exists()) {
    //         avatarDateFile.mkdir();
    //     }
    //
    //     String originalFilename = avatar.getOriginalFilename();
    //     String fileName = UUID.randomUUID().toString() + "-" + originalFilename;
    //     String avatarRealPath = avatarDatePath + "/" + fileName;
    //
    //     File file = new File(avatarRealPath);
    //
    //     try {
    //         avatar.transferTo(file);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         throw new BusinessException(SYSTEM_ERROR, "图片转存失败");
    //     }
    //
    //     String avatarUrl = "/" + yearMonth + "/" + fileName;
    //     boolean update = this.update().set("avatarUrl", avatarUrl).eq("id", loginUser.getId()).update();
    //     if(!update) {
    //         throw new BusinessException(SYSTEM_ERROR, "用户图片保存失败");
    //     }
    //     return true;
    // }

    // 通过sql语句查询标签用户
    @Deprecated
    private List<User> searchUsersByTagsSQL(List<String> tagList) {
        if (tagList.isEmpty())
            throw new BusinessException(PARAMETER_ERROR);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String s : tagList) {
            queryWrapper.like("tags", s);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafeUser).collect(Collectors.toList());
    }
}