package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.ActivityCashBack;
import com.qiqilm.server.admin.mapper.ActivityCashBackMapper;
import com.qiqilm.server.admin.service.IActivityCashBackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 【返现活动】Service业务层处理
 *
 * @author 77tv
 * @date 2021-06-07
 */
@Service
public class ActivityCashBackServiceImpl implements IActivityCashBackService {
    @Resource
    private ActivityCashBackMapper activityCashBackMapper;

    /**
     * 查询【返现活动】
     *
     * @param id 【返现活动】ID
     * @return 【返现活动】
     */
    @Override
    public ActivityCashBack selectActivityCashBackById(Long id) {
        return activityCashBackMapper.selectActivityCashBackById(id);
    }

    /**
     * 查询【返现活动】列表
     *
     * @param activityCashBack 【返现活动】
     * @return 【返现活动】
     */
    @Override
    public List<ActivityCashBack> selectActivityCashBackList(ActivityCashBack activityCashBack) {
        return activityCashBackMapper.selectActivityCashBackList(activityCashBack);
    }

    /**
     * 新增【返现活动】
     *
     * @param activityCashBack 【返现活动】
     * @return 结果
     */
    @Override
    public int insertActivityCashBack(ActivityCashBack activityCashBack) {
        return activityCashBackMapper.insertActivityCashBack(activityCashBack);
    }

    /**
     * 修改【返现活动】
     *
     * @param activityCashBack 【返现活动】
     * @return 结果
     */
    @Override
    public int updateActivityCashBack(ActivityCashBack activityCashBack) {
        return activityCashBackMapper.updateActivityCashBack(activityCashBack);
    }

    /**
     * 批量删除【返现活动】
     *
     * @param ids 需要删除的【返现活动】ID
     * @return 结果
     */
    @Override
    public int deleteActivityCashBackByIds(Long[] ids) {
        return activityCashBackMapper.deleteActivityCashBackByIds(ids);
    }

    /**
     * 删除【返现活动】信息
     *
     * @param id 【返现活动】ID
     * @return 结果
     */
    @Override
    public int deleteActivityCashBackById(Long id) {
        return activityCashBackMapper.deleteActivityCashBackById(id);
    }
}
