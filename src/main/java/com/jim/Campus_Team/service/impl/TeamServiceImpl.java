package com.jim.Campus_Team.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jim.Campus_Team.entity.domain.Team;
import com.jim.Campus_Team.entity.domain.User;
import com.jim.Campus_Team.entity.domain.UserTeam;
import com.jim.Campus_Team.entity.enums.TeamStatusEnum;
import com.jim.Campus_Team.entity.request.*;
import com.jim.Campus_Team.entity.vo.TeamUserVO;
import com.jim.Campus_Team.entity.vo.UserVO;
import com.jim.Campus_Team.exception.BusinessException;
import com.jim.Campus_Team.mapper.TeamMapper;
import com.jim.Campus_Team.service.TeamService;
import com.jim.Campus_Team.service.UserService;
import com.jim.Campus_Team.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jim.Campus_Team.common.ErrorCode.*;

/**
 * @author Jim_Lam
 * &#064;description  针对表【team】的数据库操作Service实现
 * &#064;createDate  2024-05-03 22:57:49
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTeam(AddTeamRequest teamRequest, User loginUser) {
        // 1. 请求参数是否为空
        if (teamRequest == null)
            throw new BusinessException(NULL_ERROR);
        // 2. 是否已经登录
        if (loginUser == null)
            throw new BusinessException(NOT_LOGIN);
        // 3. 校验信息
        //      1. 最大人数要求 maxNum >= 1 && maxNum <= 20
        int maxNum = Optional.ofNullable(teamRequest.getMaxNum()).orElse(0); // 因为maxNum是Integer的包装类
        if (maxNum < 1 || maxNum > 20)
            throw new BusinessException(PARAMETER_ERROR, "队伍人数不符合要求");
        //      2. 队伍名称不能超过20
        String teamName = teamRequest.getTeamName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20)
            throw new BusinessException(PARAMETER_ERROR, "队伍名称长度不符合要求");
        //      3. 队伍描述不能超过100
        String description = teamRequest.getDescription();
        if (StringUtils.isBlank(description) || description.length() > 100)
            throw new BusinessException(PARAMETER_ERROR, "队伍描述为空或过长");
        //      4. status 是否公开, 不传值默认0(公开)
        int status = Optional.ofNullable(teamRequest.getTeamStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null)
            throw new BusinessException(PARAMETER_ERROR, "队伍状态不符合要求");
        //      5. 如果 status 状态为加密，则需要密码，密码长度不能大于16位
        String teamPassword = teamRequest.getTeamPassword();
        if (statusEnum.equals(TeamStatusEnum.SECRET))
            if (StringUtils.isBlank(teamPassword) || teamPassword.length() > 16 || teamPassword.length() < 4)
                throw new BusinessException(PARAMETER_ERROR, "队伍密码长度必须为4-16位");
        // 过期时间（默认10天）>=当前时间（这里会有一个问题，mysql数据库会加8h）
        Date expireTime = teamRequest.getExpireTime();
        if (new Date().after(expireTime))
            throw new BusinessException(PARAMETER_ERROR, "当前时间在过期时间之后，不符合要求");
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        long userId = loginUser.getId();
        queryWrapper.eq("userId", userId);

        // 校验用户最多创建5个队伍
        synchronized (String.valueOf(userId).intern()) {
            long count = this.count(queryWrapper);
            if (count >= 5)
                throw new BusinessException(PARAMETER_ERROR, "一个用户最多创建5个队伍");
            //  插入队伍信息到队伍表
            Team team = new Team();
            BeanUtils.copyProperties(teamRequest, team);
            team.setId(null);
            team.setUserId(userId);
            boolean save = this.save(team);
            //  插入用户 => 队伍关系到关系表
            if (!save)
                throw new BusinessException(PARAMETER_ERROR, "创建队伍失败");
            UserTeam userTeam = new UserTeam();
            userTeam.setTeamId(team.getId());
            userTeam.setUserId(userId);
            return userTeamService.save(userTeam);
        }
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQueryRequest teamQueryRequest, boolean isAdmin) {
        // teamQueryRequest 空值默认查询全部
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 组合查询条件
        if (teamQueryRequest != null) {
            Long id = teamQueryRequest.getId();
            if (id != null && id > 0)
                queryWrapper.eq("id", id);

            // idList的使用
            String teamName = teamQueryRequest.getTeamName();
            if (StringUtils.isNotBlank(teamName))
                queryWrapper.like("teamName", teamName);
            String description = teamQueryRequest.getDescription();
            if (StringUtils.isNotBlank(description))
                queryWrapper.like("description", description);
            String searchText = teamQueryRequest.getSearchText();
            if (searchText != null) {
                queryWrapper.and(qw -> qw.like("teamName", searchText).or().like("description", searchText));
            }
            Integer maxNum = teamQueryRequest.getMaxNum();
            if (maxNum != null && maxNum > 0)
                queryWrapper.eq("maxNum", maxNum);
            Long userId = teamQueryRequest.getUserId();
            if (userId != null && userId > 0)
                queryWrapper.eq("userId", userId);
            Integer teamStatus = teamQueryRequest.getTeamStatus();
            if (teamStatus == null)
                teamStatus = 0; // 默认搜索公开队伍
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
            if (statusEnum == null)
                statusEnum = TeamStatusEnum.PUBLIC;
            if (!isAdmin && statusEnum == TeamStatusEnum.PRIVATE)
                throw new BusinessException(NO_AUTO, "没有权限");
            queryWrapper.eq("teamStatus", statusEnum.getValue());
        }
        // 不展示已过期的队伍
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));

        // 将队伍和创建者关联
        // todo 通过SQL语句优化下面关联查询
        List<Team> teamList = this.list(queryWrapper); // 搜索出的队伍

        if (CollectionUtils.isEmpty(teamList))
            return new ArrayList<>();
        ArrayList<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            if (team.getUserId() <= 0)
                continue;
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 更换时间格式
            teamUserVO.setCreateTime(userService.setTimeFormat(team.getCreateTime()));
            teamUserVO.setUpdateTime(userService.setTimeFormat(team.getUpdateTime()));
            teamUserVO.setExpireTime(userService.setTimeFormat(team.getExpireTime()));
            User user = userService.getById(team.getUserId());
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean teamUpdate(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null)
            throw new BusinessException(NULL_ERROR);
        Long teamId = teamUpdateRequest.getId();
        if (teamId == null || teamId <= 0)
            throw new BusinessException(PARAMETER_ERROR);
        Team oldTeam = this.getById(teamId);
        if (oldTeam == null)
            throw new BusinessException(PARAMETER_ERROR, "修改的队伍不存在");
        Long userId = loginUser.getId();
        if (userId == null)
            throw new BusinessException(NOT_LOGIN, "userId不存在");
        if (!Objects.equals(oldTeam.getUserId(), loginUser.getId()) && !userService.isAdmin(loginUser))
            throw new BusinessException(NO_AUTO);
        Integer teamStatus = teamUpdateRequest.getTeamStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
        if (TeamStatusEnum.SECRET.equals(statusEnum))
            if (StringUtils.isBlank(teamUpdateRequest.getTeamPassword()))
                throw new BusinessException(PARAMETER_ERROR, "加密的队伍必须设置密码");
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser) {
        if (joinTeamRequest == null)
            throw new BusinessException(PARAMETER_ERROR);
        final Long teamId = joinTeamRequest.getTeamId();
        if (teamId == null || teamId <= 0)
            throw new BusinessException(PARAMETER_ERROR);
        Team team = this.getById(teamId);
        // 判断队伍是否过期
        Date expireTime = team.getExpireTime();
        if (expireTime == null || expireTime.before(new Date()))
            throw new BusinessException(PARAMETER_ERROR, "已过期的队伍不能加入");
        Integer teamStatus = team.getTeamStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            throw new BusinessException(PARAMETER_ERROR, "私有的队伍不能加入");
        }
        String teamPassword = team.getTeamPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum))
            if (StringUtils.isBlank(teamPassword) || !teamPassword.equals(joinTeamRequest.getTeamPassword()))
                throw new BusinessException(PARAMETER_ERROR, "密码错误");

        // 锁用户、队伍
        // 使用分布式锁，避免又查数据库 更慢
        RLock lock = redissonClient.getLock("join_team");
        // 校验用户最多创建5个队伍
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                    final long userId = loginUser.getId();
                    userTeamQueryWrapper.eq("userId", userId);
                    long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
                    if (hasJoinNum >= 5)
                        throw new BusinessException(PARAMETER_ERROR, "加入和创建的队伍最多为5个");
                    userTeamQueryWrapper.eq("userId", userId);
                    userTeamQueryWrapper.eq("teamId", joinTeamRequest.getTeamId());
                    long hasJoinTeam = userTeamService.count(userTeamQueryWrapper);
                    if (hasJoinTeam > 0)
                        throw new BusinessException(PARAMETER_ERROR, "不能重复加入已经加入的队伍");
                    // 检查队伍人数是否已满
                    long teamUserNum = countTeamUserByTeamId(teamId);
                    Integer maxNum = team.getMaxNum();
                    if (teamUserNum >= maxNum)
                        throw new BusinessException(PARAMETER_ERROR, "队伍人数已满");
                    // 修改队伍信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setTeamId(teamId);
                    userTeam.setUserId(userId);
                    return userTeamService.save(userTeam);
                }
            }
        } catch (InterruptedException e) {
            return false;
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("释放锁:............................................." + Thread.currentThread().getName());
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null)
            throw new BusinessException(PARAMETER_ERROR);
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        userTeamQueryWrapper.eq("userId", userId);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count <= 0)
            throw new BusinessException(PARAMETER_ERROR, "未加入该队伍");
        // 检查队伍有几人
        long hasTeamJoinNum = countTeamUserByTeamId(teamId);
        // 队伍只有一人，退出后队伍解散
        if (hasTeamJoinNum == 1) {
            this.removeById(teamId);
        } else {
            // 队伍至少还剩两人
            // 如果自己是队长
            if (userId == team.getUserId()) {
                QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId", teamId);
                queryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1)
                    throw new BusinessException(SYSTEM_ERROR);
                UserTeam userTeam = userTeamList.get(1);
                Team updateTeam = new Team();
                updateTeam.setUserId(userTeam.getUserId());
                updateTeam.setId(teamId);
                boolean result = this.updateById(updateTeam);
                if (!result)
                    throw new BusinessException(SYSTEM_ERROR, "更新队伍队长失败");
            }
        }
        // 关联表的信息
        return userTeamService.remove(userTeamQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long teamId, User loginUser) {
        Team team = getTeamById(teamId);
        long userId = loginUser.getId();
        if (!userService.isAdmin(loginUser) && team.getUserId() != userId)
            throw new BusinessException(NO_AUTO, "没权限删除队伍");
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(queryWrapper);
        if (!result)
            throw new BusinessException(SYSTEM_ERROR, "删除队伍关联信息失败");
        // 删除队伍
        return this.removeById(teamId);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadAvatar(MultipartFile file, User loginUser, long teamId) {
        Team team = getTeamById(teamId);
        if(!loginUser.getId().equals(team.getUserId())) {
            throw new BusinessException(NO_AUTO);
        }
        return userService.uploadAvatar(file, loginUser, "teamAvatar");
    }


    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0)
            throw new BusinessException(PARAMETER_ERROR);
        Team team = this.getById(teamId);
        if (team == null)
            throw new BusinessException(PARAMETER_ERROR, "该队伍不存在");
        return team;
    }

    private long countTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }
}




