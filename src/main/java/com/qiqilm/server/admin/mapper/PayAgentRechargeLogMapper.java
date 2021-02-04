package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentRechargeLog;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface PayAgentRechargeLogMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param orderNo 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayAgentRechargeLog selectPayAgentRechargeLogById(String orderNo);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payAgentRechargeLog 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayAgentRechargeLog> selectPayAgentRechargeLogList(PayAgentRechargeLog payAgentRechargeLog);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payAgentRechargeLog 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayAgentRechargeLog(PayAgentRechargeLog payAgentRechargeLog);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payAgentRechargeLog 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayAgentRechargeLog(PayAgentRechargeLog payAgentRechargeLog);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param orderNo 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeLogById(String orderNo);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param orderNos 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeLogByIds(String[] orderNos );

	PayAgentRechargeLog count(PayAgentRechargeLog payAgentRechargeLog);
}
