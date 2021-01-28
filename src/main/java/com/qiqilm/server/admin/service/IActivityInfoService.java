package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ActivityInfo;

/**
 * 活动信息Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IActivityInfoService {
	/**
	 * 查询活动信息
	 *
	 * @param id 活动信息ID
	 * @return 活动信息
	 */
	public ActivityInfo selectActivityInfoById(String id);

	/**
	 * 查询活动信息列表
	 *
	 * @param activityInfo 活动信息
	 * @return 活动信息集合
	 */
	public List<ActivityInfo> selectActivityInfoList(ActivityInfo activityInfo);

	/**
	 * 新增活动信息
	 *
	 * @param activityInfo 活动信息
	 * @return 结果
	 */
	public int insertActivityInfo(ActivityInfo activityInfo);

	/**
	 * 修改活动信息
	 *
	 * @param activityInfo 活动信息
	 * @return 结果
	 */
	public int updateActivityInfo(ActivityInfo activityInfo);

	/**
	 * 批量删除活动信息
	 *
	 * @param ids 需要删除的活动信息ID
	 * @return 结果
	 */
	public int deleteActivityInfoByIds(String[] ids );

	/**
	 * 删除活动信息信息
	 *
	 * @param id 活动信息ID
	 * @return 结果
	 */
	public int deleteActivityInfoById(String id);
}
