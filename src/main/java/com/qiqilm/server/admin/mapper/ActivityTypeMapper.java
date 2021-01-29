package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ActivityType;

/**
 * 活动信息Mapper接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface ActivityTypeMapper {
	/**
	 * 查询活动信息
	 *
	 * @param id 活动信息ID
	 * @return 活动信息
	 */
	public ActivityType selectActivityTypeById(String id);

	/**
	 * 查询活动信息列表
	 *
	 * @param activityType 活动信息
	 * @return 活动信息集合
	 */
	public List<ActivityType> selectActivityTypeList(ActivityType activityType);

	/**
	 * 新增活动信息
	 *
	 * @param activityType 活动信息
	 * @return 结果
	 */
	public int insertActivityType(ActivityType activityType);

	/**
	 * 修改活动信息
	 *
	 * @param activityType 活动信息
	 * @return 结果
	 */
	public int updateActivityType(ActivityType activityType);

	/**
	 * 删除活动信息
	 *
	 * @param id 活动信息ID
	 * @return 结果
	 */
	public int deleteActivityTypeById(String id);

	/**
	 * 批量删除活动信息
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteActivityTypeByIds(String[] ids );

	//给活动信息做的下拉框
	List<ActivityType> selectActivityType();
}
