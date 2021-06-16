package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ActivityCashBack;

/**
 * 【返现活动】Service接口
 *
 * @author 77tv
 * @date 2021-06-07
 */
public interface IActivityCashBackService {
	/**
	 * 查询【返现活动】
	 *
	 * @param id 【返现活动】ID
	 * @return 【返现活动】
	 */
	public ActivityCashBack selectActivityCashBackById(Long id);

	/**
	 * 查询【返现活动】列表
	 *
	 * @param activityCashBack 【返现活动】
	 * @return 【返现活动】集合
	 */
	public List<ActivityCashBack> selectActivityCashBackList(ActivityCashBack activityCashBack);

	/**
	 * 新增【返现活动】
	 *
	 * @param activityCashBack 【返现活动】
	 * @return 结果
	 */
	public int insertActivityCashBack(ActivityCashBack activityCashBack);

	/**
	 * 修改【返现活动】
	 *
	 * @param activityCashBack 【返现活动】
	 * @return 结果
	 */
	public int updateActivityCashBack(ActivityCashBack activityCashBack);

	/**
	 * 批量删除【返现活动】
	 *
	 * @param ids 需要删除的【返现活动】ID
	 * @return 结果
	 */
	public int deleteActivityCashBackByIds(Long[] ids );

	/**
	 * 删除【返现活动】信息
	 *
	 * @param id 【返现活动】ID
	 * @return 结果
	 */
	public int deleteActivityCashBackById(Long id);
}
