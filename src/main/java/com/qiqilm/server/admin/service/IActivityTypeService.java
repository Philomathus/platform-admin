package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ActivityType;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IActivityTypeService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public ActivityType selectActivityTypeById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param activityType 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ActivityType> selectActivityTypeList(ActivityType activityType);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param activityType 【请填写功能名称】
	 * @return 结果
	 */
	public int insertActivityType(ActivityType activityType);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param activityType 【请填写功能名称】
	 * @return 结果
	 */
	public int updateActivityType(ActivityType activityType);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteActivityTypeByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteActivityTypeById(String id);
}
