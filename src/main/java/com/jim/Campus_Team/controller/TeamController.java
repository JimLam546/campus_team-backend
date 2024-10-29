package com.jim.Campus_Team.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jim.Campus_Team.common.BaseResponse;
import com.jim.Campus_Team.common.ResultUtil;
import com.jim.Campus_Team.entity.domain.Team;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.domain.UserTeam;
import com.jim.Campus_Team.entity.request.*;
import com.jim.Campus_Team.entity.vo.TeamUserVO;
import com.jim.Campus_Team.entity.vo.UserVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.service.TeamService;
import com.jim.Campus_Team.service.UserService;
import com.jim.Campus_Team.service.UserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jim.Campus_Team.common.ErrorCode.*;
import static com.jim.Campus_Team.contant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://47.115.163.154:80", "http://47.115.163.154:5173", "http://localhost:5173"}, allowCredentials = "true")
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addTeam(@RequestBody AddTeamRequest teamRequest, HttpServletRequest request) {
        if(teamRequest == null)
            throw new BusinessException(NULL_ERROR);
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.addTeam(teamRequest, loginUser);
        if (!result)
            throw new BusinessException(PARAMETER_ERROR, "创建队伍失败");
        return new BaseResponse<>(0, true);
    }

    @GetMapping("/get")
    public BaseResponse<TeamUserVO> getTeamById(long id) {
        if(id <= 0)
            throw new BusinessException(PARAMETER_ERROR);
        Team team = teamService.getById(id);
        User createUser = userService.getById(team.getUserId());
        TeamUserVO teamUserVO = BeanUtil.copyProperties(team, TeamUserVO.class, "expireTime");
        teamUserVO.setExpireTime(userService.setTimeFormat(team.getExpireTime()));
        teamUserVO.setCreateUser(BeanUtil.copyProperties(createUser, UserVO.class));
        // 加入队伍的用户
        List<UserTeam> teamId = userTeamService.query().eq("teamId", team.getId()).list();
        List<Long> userIdList = teamId.stream().map(UserTeam::getUserId).collect(Collectors.toList());
        List<User> userList = userService.query().in("id", userIdList).list();
        List<UserVO> userVOList = userList.stream().map(user -> BeanUtil.copyProperties(user, UserVO.class)).collect(Collectors.toList());
        teamUserVO.setTeamUserList(userVOList);
        return ResultUtil.success(teamUserVO);
    }


    @GetMapping("/list/page")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        if(teamQueryRequest == null)
            throw new BusinessException(NULL_ERROR);
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeams(teamQueryRequest, isAdmin);
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 用于前端判断登录用户是否已经加入查询出的队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<UserTeam>();
        User loginUser = userService.getLoginUser(request);
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.in("teamId", teamIdList);
        // 登录用户已经加入的队伍集合
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        Set<Long> hasJoinTeamSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
        teamList.forEach(team -> {
            // System.out.println(team);
            boolean isHasJoin = hasJoinTeamSet.contains(team.getId());
            team.setHasJoin(isHasJoin);
        });
        // 获取队伍加入的人数
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeams = userTeamService.list(userTeamQueryWrapper);
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeams.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach((team) -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size()));
        return ResultUtil.success(teamList);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if(teamUpdateRequest == null)
            throw new BusinessException(NULL_ERROR);
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        boolean result = teamService.teamUpdate(teamUpdateRequest, loginUser);
        if(!result)
            throw new BusinessException(SYSTEM_ERROR, "更新失败");
        return ResultUtil.success(true);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request) {
        if (joinTeamRequest == null)
            throw new BusinessException(PARAMETER_ERROR);
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(joinTeamRequest, loginUser);
        if(!result)
            throw new BusinessException(SYSTEM_ERROR, "加入队伍失败");
        return ResultUtil.success(true);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if(teamQuitRequest == null)
            throw new BusinessException(PARAMETER_ERROR);
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        if(!result)
            throw new BusinessException(SYSTEM_ERROR, "退出队伍失败");
        return ResultUtil.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteTeamRequest deleteTeamRequest, HttpServletRequest request) {
        if(deleteTeamRequest == null || deleteTeamRequest.getTeamId() <= 0)
            throw new BusinessException(PARAMETER_ERROR);
        User loginUser = userService.getLoginUser(request);
        long teamId = deleteTeamRequest.getTeamId();
        boolean result = teamService.deleteTeam(teamId, loginUser);
        if(!result)
            throw new BusinessException(SYSTEM_ERROR, "队伍删除失败");
        return ResultUtil.success(true);
    }

    /**
     * 我所在的队伍
     * @param request
     * @return
     */
    @GetMapping("/list/myTeam")
    public BaseResponse<List<TeamUserVO>> hasJoinTeamList(HttpServletRequest request) {
        // 获取加入队伍还没有实现可以搜索
        // if(teamQueryRequest == null)
        //     throw new BusinessException(PARAMETER_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<UserTeam> userTeams = userTeamService.query().eq("userId", loginUser.getId()).list();
        if(CollectionUtils.isEmpty(userTeams))
            return ResultUtil.success(new ArrayList<>());

        // 根据teamId分组
        List<Long> teamIdList = userTeams.stream().map((UserTeam::getTeamId)).collect(Collectors.toList());
        List<Team> teamList = teamService.query().in("id", teamIdList).list();
        List<TeamUserVO> teamUserVOs = teamList.stream().map((team) -> {
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 更新队长VO信息
            User user = userService.query().eq("id", team.getUserId()).one();
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            teamUserVO.setCreateUser(userVO);
            // 更换时间格式
            teamUserVO.setCreateTime(userService.setTimeFormat(team.getCreateTime()));
            teamUserVO.setUpdateTime(userService.setTimeFormat(team.getUpdateTime()));
            teamUserVO.setExpireTime(userService.setTimeFormat(team.getExpireTime()));

            // 获取我的队伍列中的队伍人数
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teamId", team.getId());
            teamUserVO.setHasJoinNum((int) userTeamService.count(queryWrapper));
            // 设置我是否加入队伍
            teamUserVO.setHasJoin(true);
            return teamUserVO;
        }).collect(Collectors.toList());
        return ResultUtil.success(teamUserVOs);
    }

    /**
     * 我创建的队伍
     * @param request
     * @return
     */
    @GetMapping("/list/myCreate")
    public BaseResponse<List<TeamUserVO>> hasCreateTeamList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<UserTeam> userTeams = userTeamService.query().eq("userId", loginUser.getId()).list();
        if(CollectionUtils.isEmpty(userTeams))
            return ResultUtil.success(new ArrayList<>());

        // 根据teamId分组
        List<Long> teamIdList = userTeams.stream().map((UserTeam::getTeamId)).collect(Collectors.toList());
        List<Team> teamList = teamService.query().in("id", teamIdList).list();
        // 如果自己是队长则过滤掉
        teamList = teamList.stream()
                .filter(team -> loginUser.getId().equals(team.getUserId()))
                .collect(Collectors.toList());
        List<TeamUserVO> teamUserVOs = teamList.stream().map((team) -> {
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 更新队长VO信息
            User user = userService.query().eq("id", team.getUserId()).one();
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            teamUserVO.setCreateUser(userVO);
            // 更换时间格式
            teamUserVO.setCreateTime(userService.setTimeFormat(team.getCreateTime()));
            teamUserVO.setUpdateTime(userService.setTimeFormat(team.getUpdateTime()));
            teamUserVO.setExpireTime(userService.setTimeFormat(team.getExpireTime()));

            // 获取我的队伍列中的队伍人数
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teamId", team.getId());
            teamUserVO.setHasJoinNum((int) userTeamService.count(queryWrapper));
            // 设置我是否加入队伍
            teamUserVO.setHasJoin(true);
            return teamUserVO;
        }).collect(Collectors.toList());
        return ResultUtil.success(teamUserVOs);
    }

    /**
     * 我加入的队伍，不包括自己创建的
     * @param request
     * @return
     */
    @GetMapping("/list/myJoin")
    public BaseResponse<List<TeamUserVO>> myHasJoinTeamList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<UserTeam> userTeams = userTeamService.query().eq("userId", loginUser.getId()).list();
        if(CollectionUtils.isEmpty(userTeams))
            return ResultUtil.success(new ArrayList<>());

        // 根据teamId分组
        List<Long> teamIdList = userTeams.stream().map((UserTeam::getTeamId)).collect(Collectors.toList());
        List<Team> teamList = teamService.query().in("id", teamIdList).list();
        // 如果自己是队长则过滤掉
        teamList = teamList.stream()
                .filter(team -> !loginUser.getId().equals(team.getUserId()))
                .collect(Collectors.toList());
        List<TeamUserVO> teamUserVOs = teamList.stream().map((team) -> {
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 更新队长VO信息
            User user = userService.query().eq("id", team.getUserId()).one();
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            teamUserVO.setCreateUser(userVO);
            // 更换时间格式
            teamUserVO.setCreateTime(userService.setTimeFormat(team.getCreateTime()));
            teamUserVO.setUpdateTime(userService.setTimeFormat(team.getUpdateTime()));
            teamUserVO.setExpireTime(userService.setTimeFormat(team.getExpireTime()));

            // 获取我的队伍列中的队伍人数
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teamId", team.getId());
            teamUserVO.setHasJoinNum((int) userTeamService.count(queryWrapper));
            // 设置我是否加入队伍
            teamUserVO.setHasJoin(true);
            return teamUserVO;
        }).collect(Collectors.toList());
        return ResultUtil.success(teamUserVOs);
    }

    @PostMapping("/uploadAvatar")
    public BaseResponse<Boolean> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request, long teamId) {
        if(file.isEmpty()) {
            throw new BusinessException(PARAMETER_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 验证是否登录
        if(loginUser == null) {
            throw new BusinessException(NOT_LOGIN);
        }
        String uploadURL = teamService.uploadAvatar(file, loginUser, teamId);
        boolean result = teamService.update().set("avatarURL", uploadURL).eq("id", teamId).update();
        if(!result)
            throw new BusinessException(SYSTEM_ERROR);
        return ResultUtil.success(true);
    }
}
