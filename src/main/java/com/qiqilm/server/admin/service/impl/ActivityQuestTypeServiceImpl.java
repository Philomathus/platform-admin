package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ActivityQuestTypeMapper;
import com.qiqilm.server.admin.domain.ActivityQuestType;
import com.qiqilm.server.admin.service.IActivityQuestTypeService;

/**
 * 任务类型Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ActivityQuestTypeServiceImpl implements IActivityQuestTypeService {
    @Autowired
    private ActivityQuestTypeMapper activityQuestTypeMapper;

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
        return activityQuestTypeMapper.insertActivityQuestType(activityQuestType);
    }

    /**
     * 修改任务类型
     *
     * @param activityQuestType 任务类型
     * @return 结果
     */
    @Override
    public int updateActivityQuestType(ActivityQuestType activityQuestType) {
        return activityQuestTypeMapper.updateActivityQuestType(activityQuestType);
    }

    /**
     * 批量删除任务类型
     *
     * @param ids 需要删除的任务类型ID
     * @return 结果
     */
    @Override
    public int deleteActivityQuestTypeByIds(String[] ids) {
        return activityQuestTypeMapper.deleteActivityQuestTypeByIds(ids);
    }

    /**
     * 删除任务类型信息
     *
     * @param id 任务类型ID
     * @return 结果
     */
    @Override
    public int deleteActivityQuestTypeById(String id) {
        return activityQuestTypeMapper.deleteActivityQuestTypeById(id);
    }
}
