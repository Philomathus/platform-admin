package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentRechargeAccountLog;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IPayAgentRechargeAccountLogService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param orderNo 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayAgentRechargeAccountLog selectPayAgentRechargeAccountLogById(String orderNo);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payAgentRechargeAccountLog 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayAgentRechargeAccountLog> selectPayAgentRechargeAccountLogList(PayAgentRechargeAccountLog payAgentRechargeAccountLog);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payAgentRechargeAccountLog 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayAgentRechargeAccountLog(PayAgentRechargeAccountLog payAgentRechargeAccountLog);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payAgentRechargeAccountLog 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayAgentRechargeAccountLog(PayAgentRechargeAccountLog payAgentRechargeAccountLog);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param orderNos 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeAccountLogByIds(String[] orderNos );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param orderNo 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeAccountLogById(String orderNo);
}
