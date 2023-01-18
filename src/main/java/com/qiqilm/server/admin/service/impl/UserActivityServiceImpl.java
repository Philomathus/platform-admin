package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ActivityCacheUtil;
import com.qiqilm.server.admin.domain.UserActivity;
import com.qiqilm.server.admin.mapper.UserActivityMapper;
import com.qiqilm.server.admin.service.UserActivityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户事件信息Service业务层处理
 *
 * @author Rajesh
 * @date 2023-01-218
 */

@Service
public class UserActivityServiceImpl implements UserActivityService {

    @Resource
    private UserActivityMapper userActivityMapper;

    @Resource
    private ActivityCacheUtil activityCacheUtil;


    /**
     * 查询用户活动信息列表
     *
     * @param userActivity 用户活动信息
     * @return 用户事件信息
     */
    @Override
    public List<UserActivity> selectAllUserActivity( UserActivity userActivity ) {
        return userActivityMapper.selectAllUserActivity( userActivity );
    }

    /**
     * 添加用户活动信息
     *
     * @param userActivity 用户事件信息
     * @return 结果
     */
    @Override
    public int insert( UserActivity userActivity ) {
        return userActivityMapper.insert(userActivity);
    }

    /**
     * 修改用户事件信息
     *
     * @param userActivity 活动信息
     * @return 结果
     */
    @Override
    public int update( UserActivity userActivity ) {
        int update = userActivityMapper.update(userActivity);
        activityCacheUtil.delActiveCache( ActivityCacheUtil.ACTIVITY_USER_ACTIVITY_KEY );
        return update;
    }

    /**
     * 批量删除活动信息
     *
     * @param userId 需要删除的活动信息ID
     * @return 结果
     */
    @Override
    public int deleteUserActivityByIds( String[] userId ) {
        int user = userActivityMapper.deleteUserActivityByIds(userId);
        activityCacheUtil.delActiveCache( ActivityCacheUtil.ACTIVITY_USER_ACTIVITY_KEY );
        return user;
    }

    /**
     * 通过id信息查询用户事件
     *
     * @param id 用户事件信息ID
     * @return 用户活动信息
     */
    @Override
    public UserActivity selectByUserId( String id ) {
        return userActivityMapper.selectById(id);
    }
}
