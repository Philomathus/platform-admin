package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ConfigMoneydes;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface ConfigMoneydesMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param mdId 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public ConfigMoneydes selectConfigMoneydesById(Long mdId);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param configMoneydes 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ConfigMoneydes> selectConfigMoneydesList(ConfigMoneydes configMoneydes);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param configMoneydes 【请填写功能名称】
	 * @return 结果
	 */
	public int insertConfigMoneydes(ConfigMoneydes configMoneydes);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param configMoneydes 【请填写功能名称】
	 * @return 结果
	 */
	public int updateConfigMoneydes(ConfigMoneydes configMoneydes);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param mdId 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteConfigMoneydesById(Long mdId);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param mdIds 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteConfigMoneydesByIds(Long[] mdIds );
}
