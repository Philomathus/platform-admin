package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ActivityType;

/**
 * 活动类型Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IActivityTypeService {
	/**
	 * 查询活动类型
	 *
	 * @param id 活动类型ID
	 * @return 活动类型
	 */
	public ActivityType selectActivityTypeById(String id);

	/**
	 * 查询活动类型列表
	 *
	 * @param activityType 活动类型
	 * @return 活动类型集合
	 */
	public List<ActivityType> selectActivityTypeList(ActivityType activityType);

	/**
	 * 新增活动类型
	 *
	 * @param activityType 活动类型
	 * @return 结果
	 */
	public int insertActivityType(ActivityType activityType);

	/**
	 * 修改活动类型
	 *
	 * @param activityType 活动类型
	 * @return 结果
	 */
	public int updateActivityType(ActivityType activityType);

	/**
	 * 批量删除活动类型
	 *
	 * @param ids 需要删除的活动类型ID
	 * @return 结果
	 */
	public int deleteActivityTypeByIds(String[] ids );

	/**
	 * 删除活动类型类型
	 *
	 * @param id 活动类型ID
	 * @return 结果
	 */
	public int deleteActivityTypeById(String id);
}
