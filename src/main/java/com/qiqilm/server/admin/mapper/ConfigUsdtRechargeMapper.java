package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ConfigUsdtRecharge;

/**
 * USDT渠道 Mapper接口
 *
 * @author 77tv
 * @date 2021-09-11
 */
public interface ConfigUsdtRechargeMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public ConfigUsdtRecharge selectConfigUsdtRechargeById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param configUsdtRecharge 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ConfigUsdtRecharge> selectConfigUsdtRechargeList(ConfigUsdtRecharge configUsdtRecharge);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param configUsdtRecharge 【请填写功能名称】
	 * @return 结果
	 */
	public int insertConfigUsdtRecharge(ConfigUsdtRecharge configUsdtRecharge);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param configUsdtRecharge 【请填写功能名称】
	 * @return 结果
	 */
	public int updateConfigUsdtRecharge(ConfigUsdtRecharge configUsdtRecharge);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteConfigUsdtRechargeById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteConfigUsdtRechargeByIds(String[] ids );
}
