package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.mapper.ActivityInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.domain.ActivityInfo;
import com.qiqilm.server.admin.service.IActivityInfoService;

/**
 * 活动信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ActivityInfoServiceImpl implements IActivityInfoService {
    @Autowired
    private ActivityInfoMapper activityInfoMapper;

    /**
     * 查询活动信息
     *
     * @param id 活动信息ID
     * @return 活动信息
     */
    @Override
    public ActivityInfo selectActivityInfoById(String id) {
        return activityInfoMapper.selectActivityInfoById(id);
    }

    /**
     * 查询活动信息列表
     *
     * @param activityInfo 活动信息
     * @return 活动信息
     */
    @Override
    public List<ActivityInfo> selectActivityInfoList(ActivityInfo activityInfo) {
        return activityInfoMapper.selectActivityInfoList(activityInfo);
    }

    /**
     * 新增活动信息
     *
     * @param activityInfo 活动信息
     * @return 结果
     */
    @Override
    public int insertActivityInfo(ActivityInfo activityInfo) {
        return activityInfoMapper.insertActivityInfo(activityInfo);
    }

    /**
     * 修改活动信息
     *
     * @param activityInfo 活动信息
     * @return 结果
     */
    @Override
    public int updateActivityInfo(ActivityInfo activityInfo) {
        return activityInfoMapper.updateActivityInfo(activityInfo);
    }

    /**
     * 批量删除活动信息
     *
     * @param ids 需要删除的活动信息ID
     * @return 结果
     */
    @Override
    public int deleteActivityInfoByIds(String[] ids) {
        return activityInfoMapper.deleteActivityInfoByIds(ids);
    }

    /**
     * 删除活动信息信息
     *
     * @param id 活动信息ID
     * @return 结果
     */
    @Override
    public int deleteActivityInfoById(String id) {
        return activityInfoMapper.deleteActivityInfoById(id);
    }
}
