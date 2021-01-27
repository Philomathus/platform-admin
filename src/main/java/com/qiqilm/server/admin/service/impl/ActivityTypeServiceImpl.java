package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ActivityTypeMapper;
import com.qiqilm.server.admin.domain.ActivityType;
import com.qiqilm.server.admin.service.IActivityTypeService;

/**
 * 活动类型Service
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ActivityTypeServiceImpl implements IActivityTypeService {
    @Autowired
    private ActivityTypeMapper activityTypeMapper;

    /**
     * ѯ活动类型
     *
     * @param id 活动类型ID
     * @return 活动类型
     */
    @Override
    public ActivityType selectActivityTypeById(String id) {
        return activityTypeMapper.selectActivityTypeById(id);
    }

    /**
     * ѯ活动类型б
     *
     * @param activityType 活动类型
     * @return 活动类型
     */
    @Override
    public List<ActivityType> selectActivityTypeList(ActivityType activityType) {
        return activityTypeMapper.selectActivityTypeList(activityType);
    }

    /**
     * 活动类型
     *
     * @param activityType 活动类型
     * @return 
     */
    @Override
    public int insertActivityType(ActivityType activityType) {
        activityType.setCreateTime(DateUtils.getNowDate());
        return activityTypeMapper.insertActivityType(activityType);
    }

    /**
     * ޸活动类型
     *
     * @param activityType 活动类型
     * @return 
     */
    @Override
    public int updateActivityType(ActivityType activityType) {
        return activityTypeMapper.updateActivityType(activityType);
    }

    /**
     * ɾ活动类型
     *
     * @param ids 活动类型ID
     * @return 
     */
    @Override
    public int deleteActivityTypeByIds(String[] ids) {
        return activityTypeMapper.deleteActivityTypeByIds(ids);
    }

    /**
     * ɾ活动类型Ϣ
     *
     * @param id 活动类型ID
     * @return 
     */
    @Override
    public int deleteActivityTypeById(String id) {
        return activityTypeMapper.deleteActivityTypeById(id);
    }
}
