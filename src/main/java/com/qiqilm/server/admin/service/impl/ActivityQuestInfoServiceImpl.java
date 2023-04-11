package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ActivityCacheUtil;
import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.domain.ActivityInfo;
import com.qiqilm.server.admin.domain.ActivityQuestInfo;
import com.qiqilm.server.admin.domain.GameInfo;
import com.qiqilm.server.admin.mapper.ActivityQuestInfoMapper;
import com.qiqilm.server.admin.service.IActivityQuestInfoService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 任务信息列表Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ActivityQuestInfoServiceImpl implements IActivityQuestInfoService {
    @Resource
    private ActivityQuestInfoMapper activityQuestInfoMapper;
    @Autowired
    private ConfigDomainCacheUtil   configDomainCacheUtil;
    @Resource
    private ActivityCacheUtil       activityCacheUtil;

    /**
     * 查询任务信息列表
     *
     * @param id 任务信息列表ID
     *
     * @return 任务信息列表
     */
    @Override
    public ActivityQuestInfo selectActivityQuestInfoById( String id ) {
        return activityQuestInfoMapper.selectActivityQuestInfoById( id );
    }

    /**
     * 查询任务信息列表列表
     *
     * @param activityQuestInfo 任务信息列表
     *
     * @return 任务信息列表
     */
    @Override
    public List<ActivityQuestInfo> selectActivityQuestInfoList( ActivityQuestInfo activityQuestInfo ) {
        List<ActivityQuestInfo> activityQuestInfos = activityQuestInfoMapper.selectActivityQuestInfoList( activityQuestInfo );
        if ( !CollectionUtils.isEmpty( activityQuestInfos ) ) {
            String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
            for ( ActivityQuestInfo info : activityQuestInfos ) {
                if ( StringUtils.isNotBlank( info.getIcon() ) && !info.getIcon().startsWith( "http" ) ) {
                    info.setIcon( domainValue + info.getIcon() );
                }
            }
        }
        return activityQuestInfos;
    }

    /**
     * 新增任务信息列表
     *
     * @param activityQuestInfo 任务信息列表
     *
     * @return 结果
     */
    @Override
    public int insertActivityQuestInfo( ActivityQuestInfo activityQuestInfo ) {
        int i = activityQuestInfoMapper.insertActivityQuestInfo( activityQuestInfo );
        activityCacheUtil.delActiveCache( ActivityCacheUtil.ACTIVITY_QUEST_INFO_KEY );
        return i;
    }

    /**
     * 修改任务信息列表
     *
     * @param activityQuestInfo 任务信息列表
     *
     * @return 结果
     */
    @Override
    public int updateActivityQuestInfo( ActivityQuestInfo activityQuestInfo ) {
        int i = activityQuestInfoMapper.updateActivityQuestInfo( activityQuestInfo );
        activityCacheUtil.delActiveCache( ActivityCacheUtil.ACTIVITY_QUEST_INFO_KEY );
        return i;
    }

    /**
     * 批量删除任务信息列表
     *
     * @param ids 需要删除的任务信息列表ID
     *
     * @return 结果
     */
    @Override
    public int deleteActivityQuestInfoByIds( String[] ids ) {
        int i = activityQuestInfoMapper.deleteActivityQuestInfoByIds( ids );
        activityCacheUtil.delActiveCache( ActivityCacheUtil.ACTIVITY_QUEST_INFO_KEY );
        return i;
    }

    /**
     * 删除任务信息列表信息
     *
     * @param id 任务信息列表ID
     *
     * @return 结果
     */
    @Override
    public int deleteActivityQuestInfoById( String id ) {
        int i = activityQuestInfoMapper.deleteActivityQuestInfoById( id );
        activityCacheUtil.delActiveCache( ActivityCacheUtil.ACTIVITY_QUEST_INFO_KEY );
        return i;
    }

    @Override
    public List<ActivityQuestInfo> titleSelect() {
        return activityQuestInfoMapper.titleSelect();
    }
}
