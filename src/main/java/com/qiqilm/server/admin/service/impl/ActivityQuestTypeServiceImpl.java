package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ActivityCacheUtil;
import com.qiqilm.server.admin.domain.ActivityQuestType;
import com.qiqilm.server.admin.mapper.ActivityQuestTypeMapper;
import com.qiqilm.server.admin.service.IActivityQuestTypeService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 任务类型Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ActivityQuestTypeServiceImpl implements IActivityQuestTypeService {
    @Resource
    private ActivityQuestTypeMapper activityQuestTypeMapper;
    @Resource
    private ActivityCacheUtil activityCacheUtil;
    /**
     * 查询任务类型
     *
     * @param id 任务类型ID
     * @return 任务类型
     */
    @Override
    public ActivityQuestType selectActivityQuestTypeById(String id) {
        return activityQuestTypeMapper.selectActivityQuestTypeById(id);
    }

    /**
     * 查询任务类型列表
     *
     * @param activityQuestType 任务类型
     * @return 任务类型
     */
    @Override
    public List<ActivityQuestType> selectActivityQuestTypeList(ActivityQuestType activityQuestType) {
        return activityQuestTypeMapper.selectActivityQuestTypeList(activityQuestType);
    }

    /**
     * 新增任务类型
     *
     * @param activityQuestType 任务类型
     * @return 结果
     */
    @Override
    public int insertActivityQuestType(ActivityQuestType activityQuestType) {
        activityQuestType.setCreateTime(DateUtils.getNowDate());
        int i = activityQuestTypeMapper.insertActivityQuestType(activityQuestType);
        activityCacheUtil.delActiveCache(ActivityCacheUtil.ACTIVITY_QUEST_TYPE_KEY);
        return i;
    }

    /**
     * 修改任务类型
     *
     * @param activityQuestType 任务类型
     * @return 结果
     */
    @Override
    public int updateActivityQuestType(ActivityQuestType activityQuestType) {
        int i = activityQuestTypeMapper.updateActivityQuestType(activityQuestType);
        activityCacheUtil.delActiveCache(ActivityCacheUtil.ACTIVITY_QUEST_TYPE_KEY);
        return i;
    }

    /**
     * 批量删除任务类型
     *
     * @param ids 需要删除的任务类型ID
     * @return 结果
     */
    @Override
    public int deleteActivityQuestTypeByIds(String[] ids) {
        int i = activityQuestTypeMapper.deleteActivityQuestTypeByIds(ids);
        activityCacheUtil.delActiveCache(ActivityCacheUtil.ACTIVITY_QUEST_TYPE_KEY);
        return i;
    }

    /**
     * 删除任务类型信息
     *
     * @param id 任务类型ID
     * @return 结果
     */
    @Override
    public int deleteActivityQuestTypeById(String id) {
        int i = activityQuestTypeMapper.deleteActivityQuestTypeById(id);
        activityCacheUtil.delActiveCache(ActivityCacheUtil.ACTIVITY_QUEST_TYPE_KEY);
        return i;
    }

    @Override
    public List<ActivityQuestType> selectActivityQuestType() {
        return activityQuestTypeMapper.selectActivityQuestType();
    }

    @Override
    public List<ActivityQuestType> nameSelect() {
        return activityQuestTypeMapper.nameSelect();
    }
}
