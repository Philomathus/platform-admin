package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentRechargeBank;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface PayAgentRechargeBankMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayAgentRechargeBank selectPayAgentRechargeBankById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payAgentRechargeBank 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayAgentRechargeBank> selectPayAgentRechargeBankList(PayAgentRechargeBank payAgentRechargeBank);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payAgentRechargeBank 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayAgentRechargeBank(PayAgentRechargeBank payAgentRechargeBank);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payAgentRechargeBank 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayAgentRechargeBank(PayAgentRechargeBank payAgentRechargeBank);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeBankById(Long id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeBankByIds(Long[] ids );
}
