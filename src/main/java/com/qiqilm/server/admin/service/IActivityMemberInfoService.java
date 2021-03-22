package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.ActivityMemberInfo;

import java.util.List;
import java.util.Map;

/**
 * 会员推广管理Service接口
 *
 * @author 77tv
 * @date 2021-03-19
 */
public interface IActivityMemberInfoService {
	/**
	 * 查询会员推广管理
	 *
	 * @param id 会员推广管理ID
	 * @return 会员推广管理
	 */
	public ActivityMemberInfo selectActivityMemberInfoById(String id);

	/**
	 * 查询会员推广管理列表
	 *
	 * @param activityMemberInfo 会员推广管理
	 * @return 会员推广管理集合
	 */
	public List<ActivityMemberInfo> selectActivityMemberInfoList(ActivityMemberInfo activityMemberInfo);

	/**
	 * 新增会员推广管理
	 *
	 * @param activityMemberInfo 会员推广管理
	 * @return 结果
	 */
	public int insertActivityMemberInfo(ActivityMemberInfo activityMemberInfo);

	/**
	 * 修改会员推广管理
	 *
	 * @param activityMemberInfo 会员推广管理
	 * @return 结果
	 */
	public int updateActivityMemberInfo(ActivityMemberInfo activityMemberInfo);

	/**
	 * 批量删除会员推广管理
	 *
	 * @param ids 需要删除的会员推广管理ID
	 * @return 结果
	 */
	public int deleteActivityMemberInfoByIds(String[] ids );

	/**
	 * 删除会员推广管理信息
	 *
	 * @param id 会员推广管理ID
	 * @return 结果
	 */
	public int deleteActivityMemberInfoById(String id);

    List<Map> selectIpList(ActivityMemberInfo activityMemberInfo);
}
