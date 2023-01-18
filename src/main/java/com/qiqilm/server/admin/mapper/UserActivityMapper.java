package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.UserActivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户活动信息Mapper接口
 *
 * @author Rajesh
 * @date 2022-01-18
 */
@Mapper
public interface UserActivityMapper {

    /**
     * 查询用户活动信息列表
     *
     * @param userActivity 用户活动信息
     * @return 用户事件信息
     */
    List<UserActivity> selectAllUserActivity( UserActivity userActivity);


    /**
     * 批量删除活动信息
     *
     * @param userId 需要删除的活动信息ID
     * @return 结果
     */
    int deleteUserActivityByIds( String[] userId );


    /**
     * 通过id信息查询用户事件
     *
     * @param id 用户事件信息ID
     * @return 用户活动信息
     */
    UserActivity selectById( String id );
}
