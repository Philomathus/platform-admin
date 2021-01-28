package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ConfigBank;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IConfigBankService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public ConfigBank selectConfigBankById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param configBank 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ConfigBank> selectConfigBankList(ConfigBank configBank);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param configBank 【请填写功能名称】
	 * @return 结果
	 */
	public int insertConfigBank(ConfigBank configBank);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param configBank 【请填写功能名称】
	 * @return 结果
	 */
	public int updateConfigBank(ConfigBank configBank);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteConfigBankByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteConfigBankById(String id);
}
