package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.domain.ActivityInfo;
import com.qiqilm.server.admin.domain.ActivityQuestInfo;
import com.qiqilm.server.admin.mapper.ActivityQuestInfoMapper;
import com.qiqilm.server.admin.service.IActivityQuestInfoService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 任务信息列表Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ActivityQuestInfoServiceImpl implements IActivityQuestInfoService {
	@Autowired
	private ActivityQuestInfoMapper activityQuestInfoMapper;
	@Autowired
	private ConfigDomainCacheUtil   configDomainCacheUtil;

	/**
	 * 查询任务信息列表
	 *
	 * @param id 任务信息列表ID
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
	 * @return 结果
	 */
	@Override
	public int insertActivityQuestInfo( ActivityQuestInfo activityQuestInfo ) {
		return activityQuestInfoMapper.insertActivityQuestInfo( activityQuestInfo );
	}

	/**
	 * 修改任务信息列表
	 *
	 * @param activityQuestInfo 任务信息列表
	 * @return 结果
	 */
	@Override
	public int updateActivityQuestInfo( ActivityQuestInfo activityQuestInfo ) {
		return activityQuestInfoMapper.updateActivityQuestInfo( activityQuestInfo );
	}

	/**
	 * 批量删除任务信息列表
	 *
	 * @param ids 需要删除的任务信息列表ID
	 * @return 结果
	 */
	@Override
	public int deleteActivityQuestInfoByIds( String[] ids ) {
		return activityQuestInfoMapper.deleteActivityQuestInfoByIds( ids );
	}

	/**
	 * 删除任务信息列表信息
	 *
	 * @param id 任务信息列表ID
	 * @return 结果
	 */
	@Override
	public int deleteActivityQuestInfoById( String id ) {
		return activityQuestInfoMapper.deleteActivityQuestInfoById( id );
	}
}
