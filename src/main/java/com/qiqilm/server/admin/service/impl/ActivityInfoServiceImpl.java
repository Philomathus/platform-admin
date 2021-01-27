package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.mapper.ActivityInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.domain.ActivityInfo;
import com.qiqilm.server.admin.service.IActivityInfoService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ActivityInfoServiceImpl implements IActivityInfoService {
    @Autowired
    private ActivityInfoMapper activityInfoMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ActivityInfo selectActivityInfoById(String id) {
        return activityInfoMapper.selectActivityInfoById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param activityInfo 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ActivityInfo> selectActivityInfoList(ActivityInfo activityInfo) {
        return activityInfoMapper.selectActivityInfoList(activityInfo);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param activityInfo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertActivityInfo(ActivityInfo activityInfo) {
        return activityInfoMapper.insertActivityInfo(activityInfo);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param activityInfo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateActivityInfo(ActivityInfo activityInfo) {
        return activityInfoMapper.updateActivityInfo(activityInfo);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteActivityInfoByIds(String[] ids) {
        return activityInfoMapper.deleteActivityInfoByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteActivityInfoById(String id) {
        return activityInfoMapper.deleteActivityInfoById(id);
    }
}
