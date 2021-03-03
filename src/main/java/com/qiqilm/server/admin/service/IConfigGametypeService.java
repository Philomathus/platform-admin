package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigGametype;

import java.util.List;



/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IConfigGametypeService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public ConfigGametype selectConfigGametypeById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param configGametype 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ConfigGametype> selectConfigGametypeList(ConfigGametype configGametype);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param configGametype 【请填写功能名称】
	 * @return 结果
	 */
	public AjaxResult insertConfigGametype(ConfigGametype configGametype);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param configGametype 【请填写功能名称】
	 * @return 结果
	 */
	public int updateConfigGametype(ConfigGametype configGametype);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteConfigGametypeByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteConfigGametypeById(String id);
}