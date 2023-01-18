package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.UserActivity;

import java.util.List;

/**
 * 用户活动信息Service接口
 *
 * @author Rajesh
 * @date 2023-01-18
 */
public interface UserActivityService {

    /**
     * 查询用户活动信息列表
     *
     * @param userActivity 用户活动列表信息
     * @return 用户活动信息收集
     */
    List<UserActivity> selectAllUserActivity( UserActivity userActivity );

    /**
     * 添加用户事件信息
     *
     * @param userActivity 用户事件信息
     * @return 结果
     */
    int insert( UserActivity userActivity );

    /**
     * 修改用户事件信息
     *
     * @param userActivity 用户事件信息
     * @return 结果
     */
    int update( UserActivity userActivity );

    /**
     * 批量删除用户活动信息
     *
     * @param userId 待删除的用户事件信息ID
     * @return 结果
     */
    int deleteUserActivityByIds( String[] userId );

    /**
     * 通过id信息查询用户事件
     *
     * @param id 用户事件信息ID
     * @return 用户活动信息
     */
    UserActivity selectByUserId( String id );
}
