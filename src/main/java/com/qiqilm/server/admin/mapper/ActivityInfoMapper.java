package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ActivityInfo;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface ActivityInfoMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public ActivityInfo selectActivityInfoById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param activityInfo 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ActivityInfo> selectActivityInfoList(ActivityInfo activityInfo);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param activityInfo 【请填写功能名称】
	 * @return 结果
	 */
	public int insertActivityInfo(ActivityInfo activityInfo);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param activityInfo 【请填写功能名称】
	 * @return 结果
	 */
	public int updateActivityInfo(ActivityInfo activityInfo);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteActivityInfoById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteActivityInfoByIds(String[] ids );
}
