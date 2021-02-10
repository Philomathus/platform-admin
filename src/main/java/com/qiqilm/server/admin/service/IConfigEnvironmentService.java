package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigEnvironment;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface IConfigEnvironmentService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param envCode 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public ConfigEnvironment selectConfigEnvironmentById(String envCode);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param configEnvironment 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ConfigEnvironment> selectConfigEnvironmentList(ConfigEnvironment configEnvironment);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param configEnvironment 【请填写功能名称】
	 * @return 结果
	 */
	public AjaxResult insertConfigEnvironment(ConfigEnvironment configEnvironment);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param configEnvironment 【请填写功能名称】
	 * @return 结果
	 */
	public int updateConfigEnvironment(ConfigEnvironment configEnvironment);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param envCodes 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteConfigEnvironmentByIds(String[] envCodes );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param envCode 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteConfigEnvironmentById(String envCode);

    public AjaxResult getTitleIndex(String title, String code);

	void refreshCache();
}
