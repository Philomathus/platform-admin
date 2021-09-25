package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentRechargeReminder;

/**
 * 代充银行提示语Service接口
 *
 * @author 77tv
 * @date 2021-09-25
 */
public interface IPayAgentRechargeReminderService {
	/**
	 * 查询代充银行提示语
	 *
	 * @param id 代充银行提示语ID
	 * @return 代充银行提示语
	 */
	public PayAgentRechargeReminder selectPayAgentRechargeReminderById(Long id);

	/**
	 * 查询代充银行提示语列表
	 *
	 * @param payAgentRechargeReminder 代充银行提示语
	 * @return 代充银行提示语集合
	 */
	public List<PayAgentRechargeReminder> selectPayAgentRechargeReminderList(PayAgentRechargeReminder payAgentRechargeReminder);

	/**
	 * 新增代充银行提示语
	 *
	 * @param payAgentRechargeReminder 代充银行提示语
	 * @return 结果
	 */
	public int insertPayAgentRechargeReminder(PayAgentRechargeReminder payAgentRechargeReminder);

	/**
	 * 修改代充银行提示语
	 *
	 * @param payAgentRechargeReminder 代充银行提示语
	 * @return 结果
	 */
	public int updatePayAgentRechargeReminder(PayAgentRechargeReminder payAgentRechargeReminder);

	/**
	 * 批量删除代充银行提示语
	 *
	 * @param ids 需要删除的代充银行提示语ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeReminderByIds(Long[] ids );

	/**
	 * 删除代充银行提示语信息
	 *
	 * @param id 代充银行提示语ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeReminderById(Long id);
}
