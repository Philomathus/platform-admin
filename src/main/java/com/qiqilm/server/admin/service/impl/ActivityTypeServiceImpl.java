package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ActivityTypeMapper;
import com.qiqilm.server.admin.domain.ActivityType;
import com.qiqilm.server.admin.service.IActivityTypeService;

/**
 * 【请填写功能名称】Serviceҵ��㴦��
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ActivityTypeServiceImpl implements IActivityTypeService {
    @Autowired
    private ActivityTypeMapper activityTypeMapper;

    /**
     * ��ѯ【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ActivityType selectActivityTypeById(String id) {
        return activityTypeMapper.selectActivityTypeById(id);
    }

    /**
     * ��ѯ【请填写功能名称】�б�
     *
     * @param activityType 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ActivityType> selectActivityTypeList(ActivityType activityType) {
        return activityTypeMapper.selectActivityTypeList(activityType);
    }

    /**
     * ����【请填写功能名称】
     *
     * @param activityType 【请填写功能名称】
     * @return ���
     */
    @Override
    public int insertActivityType(ActivityType activityType) {
        activityType.setCreateTime(DateUtils.getNowDate());
        return activityTypeMapper.insertActivityType(activityType);
    }

    /**
     * �޸�【请填写功能名称】
     *
     * @param activityType 【请填写功能名称】
     * @return ���
     */
    @Override
    public int updateActivityType(ActivityType activityType) {
        return activityTypeMapper.updateActivityType(activityType);
    }

    /**
     * ����ɾ��【请填写功能名称】
     *
     * @param ids ��Ҫɾ����【请填写功能名称】ID
     * @return ���
     */
    @Override
    public int deleteActivityTypeByIds(String[] ids) {
        return activityTypeMapper.deleteActivityTypeByIds(ids);
    }

    /**
     * ɾ��【请填写功能名称】��Ϣ
     *
     * @param id 【请填写功能名称】ID
     * @return ���
     */
    @Override
    public int deleteActivityTypeById(String id) {
        return activityTypeMapper.deleteActivityTypeById(id);
    }
}
