package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentRechargeAccount;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IPayAgentRechargeAccountService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayAgentRechargeAccount selectPayAgentRechargeAccountById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payAgentRechargeAccount 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayAgentRechargeAccount> selectPayAgentRechargeAccountList(PayAgentRechargeAccount payAgentRechargeAccount);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payAgentRechargeAccount 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayAgentRechargeAccount(PayAgentRechargeAccount payAgentRechargeAccount);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payAgentRechargeAccount 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayAgentRechargeAccount(PayAgentRechargeAccount payAgentRechargeAccount);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeAccountByIds(Long[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeAccountById(Long id);
}
