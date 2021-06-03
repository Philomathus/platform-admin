package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ActivityCacheUtil;
import com.qiqilm.server.admin.domain.ActivityType;
import com.qiqilm.server.admin.mapper.ActivityTypeMapper;
import com.qiqilm.server.admin.service.IActivityTypeService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 活动类型Service
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ActivityTypeServiceImpl implements IActivityTypeService {
    @Resource
    private ActivityTypeMapper activityTypeMapper;
    @Resource
    private ActivityCacheUtil activityCacheUtil;

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
        int i = activityTypeMapper.insertActivityType(activityType);
        activityCacheUtil.addActivityType(activityType);
        return i;
    }

    /**
     * ޸活动类型
     *
     * @param activityType 活动类型
     * @return 
     */
    @Override
    public int updateActivityType(ActivityType activityType) {
        int i = activityTypeMapper.updateActivityType(activityType);
        activityCacheUtil.delActiveCache(ActivityCacheUtil.ACTIVITY_TYPE_KEY);
        return i;
    }

    /**
     * ɾ活动类型
     *
     * @param ids 活动类型ID
     * @return 
     */
    @Override
    public int deleteActivityTypeByIds(String[] ids) {
        int i = activityTypeMapper.deleteActivityTypeByIds(ids);
        activityCacheUtil.delActiveCache(ActivityCacheUtil.ACTIVITY_TYPE_KEY);
        return i ;
    }

    /**
     * ɾ活动类型Ϣ
     *
     * @param id 活动类型ID
     * @return 
     */
    @Override
    public int deleteActivityTypeById(String id) {
        int i = activityTypeMapper.deleteActivityTypeById(id);
        activityCacheUtil.delActiveCache(ActivityCacheUtil.ACTIVITY_TYPE_KEY);
        return i;
    }

    @Override
    public List<ActivityType> selectActivityType() {
        return activityTypeMapper.selectActivityType();
    }
}
