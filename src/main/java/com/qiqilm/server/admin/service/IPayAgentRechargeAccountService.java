package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.PayAgentRechargeAccount;

/**
 * 【代充人】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IPayAgentRechargeAccountService {
	/**
	 * 查询【代充人】
	 *
	 * @param id 【代充人】ID
	 * @return 【代充人】
	 */
	public PayAgentRechargeAccount selectPayAgentRechargeAccountById(Long id);

	/**
	 * 查询【代充人】列表
	 *
	 * @param payAgentRechargeAccount 【代充人】
	 * @return 【代充人】集合
	 */
	public List<PayAgentRechargeAccount> selectPayAgentRechargeAccountList(PayAgentRechargeAccount payAgentRechargeAccount);

	/**
	 * 新增【代充人】
	 *
	 * @param payAgentRechargeAccount 【代充人】
	 * @return 结果
	 */
	public AjaxResult insertPayAgentRechargeAccount(PayAgentRechargeAccount payAgentRechargeAccount);

	/**
	 * 修改【代充人】
	 *
	 * @param payAgentRechargeAccount 【代充人】
	 * @return 结果
	 */
	public int updatePayAgentRechargeAccount(PayAgentRechargeAccount payAgentRechargeAccount);

	/**
	 * 批量删除【代充人】
	 *
	 * @param ids 需要删除的【代充人】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeAccountByIds(Long[] ids );

	/**
	 * 删除【代充人】信息
	 *
	 * @param id 【代充人】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeAccountById(Long id);
}
