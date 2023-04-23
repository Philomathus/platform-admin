package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.ActivityQuestInfo;

import java.util.List;

/**
 * 任务信息列表Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IActivityQuestInfoService {
    /**
     * 查询任务信息列表
     *
     * @param id 任务信息列表ID
     *
     * @return 任务信息列表
     */
    public ActivityQuestInfo selectActivityQuestInfoById( String id );

    /**
     * 查询任务信息列表列表
     *
     * @param activityQuestInfo 任务信息列表
     *
     * @return 任务信息列表集合
     */
    public List<ActivityQuestInfo> selectActivityQuestInfoList( ActivityQuestInfo activityQuestInfo );

    /**
     * 新增任务信息列表
     *
     * @param activityQuestInfo 任务信息列表
     *
     * @return 结果
     */
    public int insertActivityQuestInfo( ActivityQuestInfo activityQuestInfo );

    /**
     * 修改任务信息列表
     *
     * @param activityQuestInfo 任务信息列表
     *
     * @return 结果
     */
    public int updateActivityQuestInfo( ActivityQuestInfo activityQuestInfo );

    /**
     * 批量删除任务信息列表
     *
     * @param ids 需要删除的任务信息列表ID
     *
     * @return 结果
     */
    public int deleteActivityQuestInfoByIds( String[] ids );

    /**
     * 删除任务信息列表信息
     *
     * @param id 任务信息列表ID
     *
     * @return 结果
     */
    public int deleteActivityQuestInfoById( String id );

}
