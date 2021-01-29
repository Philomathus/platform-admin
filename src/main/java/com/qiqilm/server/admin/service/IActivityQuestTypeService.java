package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ActivityQuestType;
import com.qiqilm.server.admin.domain.ActivityType;

/**
 * 任务类型Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IActivityQuestTypeService {
	/**
	 * 查询任务类型
	 *
	 * @param id 任务类型ID
	 * @return 任务类型
	 */
	public ActivityQuestType selectActivityQuestTypeById(String id);

	/**
	 * 查询任务类型列表
	 *
	 * @param activityQuestType 任务类型
	 * @return 任务类型集合
	 */
	public List<ActivityQuestType> selectActivityQuestTypeList(ActivityQuestType activityQuestType);

	/**
	 * 新增任务类型
	 *
	 * @param activityQuestType 任务类型
	 * @return 结果
	 */
	public int insertActivityQuestType(ActivityQuestType activityQuestType);

	/**
	 * 修改任务类型
	 *
	 * @param activityQuestType 任务类型
	 * @return 结果
	 */
	public int updateActivityQuestType(ActivityQuestType activityQuestType);

	/**
	 * 批量删除任务类型
	 *
	 * @param ids 需要删除的任务类型ID
	 * @return 结果
	 */
	public int deleteActivityQuestTypeByIds(String[] ids );

	/**
	 * 删除任务类型信息
	 *
	 * @param id 任务类型ID
	 * @return 结果
	 */
	public int deleteActivityQuestTypeById(String id);

	//给任务信息做的下拉框
	List<ActivityQuestType> selectActivityQuestType();
}
