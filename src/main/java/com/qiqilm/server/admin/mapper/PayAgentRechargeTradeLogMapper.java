package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentRechargeTradeLog;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-02-01
 */
public interface PayAgentRechargeTradeLogMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param orderNo 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayAgentRechargeTradeLog selectPayAgentRechargeTradeLogById(String orderNo);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payAgentRechargeTradeLog 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayAgentRechargeTradeLog> selectPayAgentRechargeTradeLogList(PayAgentRechargeTradeLog payAgentRechargeTradeLog);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payAgentRechargeTradeLog 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayAgentRechargeTradeLog(PayAgentRechargeTradeLog payAgentRechargeTradeLog);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payAgentRechargeTradeLog 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayAgentRechargeTradeLog(PayAgentRechargeTradeLog payAgentRechargeTradeLog);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param orderNo 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeTradeLogById(String orderNo);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param orderNos 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeTradeLogByIds(String[] orderNos );
}
