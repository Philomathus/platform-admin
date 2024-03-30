package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.ActivityCashBack;
import com.qiqilm.server.admin.domain.rsp.RspActivityCashBack;

import java.util.List;

/**
 * 【返现活动】Mapper接口
 *
 * @author 77tv
 * @date 2021-06-07
 */
public interface ActivityCashBackMapper {
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

	public List<RspActivityCashBack> selectActivityCashBackLists(ActivityCashBack activityCashBack);

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
	 * 删除【返现活动】
	 *
	 * @param id 【返现活动】ID
	 * @return 结果
	 */
	public int deleteActivityCashBackById(Long id);

	/**
	 * 批量删除【返现活动】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteActivityCashBackByIds(Long[] ids );
}
