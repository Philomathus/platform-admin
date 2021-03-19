package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ActivityMemberInfoMapper;
import com.qiqilm.server.admin.domain.ActivityMemberInfo;
import com.qiqilm.server.admin.service.IActivityMemberInfoService;

/**
 * 会员推广管理Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-19
 */
@Service
public class ActivityMemberInfoServiceImpl implements IActivityMemberInfoService {
    @Autowired
    private ActivityMemberInfoMapper activityMemberInfoMapper;

    /**
     * 查询会员推广管理
     *
     * @param id 会员推广管理ID
     * @return 会员推广管理
     */
    @Override
    public ActivityMemberInfo selectActivityMemberInfoById(String id) {
        return activityMemberInfoMapper.selectActivityMemberInfoById(id);
    }

    /**
     * 查询会员推广管理列表
     *
     * @param activityMemberInfo 会员推广管理
     * @return 会员推广管理
     */
    @Override
    public List<ActivityMemberInfo> selectActivityMemberInfoList(ActivityMemberInfo activityMemberInfo) {
        return activityMemberInfoMapper.selectActivityMemberInfoList(activityMemberInfo);
    }

    /**
     * 新增会员推广管理
     *
     * @param activityMemberInfo 会员推广管理
     * @return 结果
     */
    @Override
    public int insertActivityMemberInfo(ActivityMemberInfo activityMemberInfo) {
        return activityMemberInfoMapper.insertActivityMemberInfo(activityMemberInfo);
    }

    /**
     * 修改会员推广管理
     *
     * @param activityMemberInfo 会员推广管理
     * @return 结果
     */
    @Override
    public int updateActivityMemberInfo(ActivityMemberInfo activityMemberInfo) {
        return activityMemberInfoMapper.updateActivityMemberInfo(activityMemberInfo);
    }

    /**
     * 批量删除会员推广管理
     *
     * @param ids 需要删除的会员推广管理ID
     * @return 结果
     */
    @Override
    public int deleteActivityMemberInfoByIds(String[] ids) {
        return activityMemberInfoMapper.deleteActivityMemberInfoByIds(ids);
    }

    /**
     * 删除会员推广管理信息
     *
     * @param id 会员推广管理ID
     * @return 结果
     */
    @Override
    public int deleteActivityMemberInfoById(String id) {
        return activityMemberInfoMapper.deleteActivityMemberInfoById(id);
    }
}
