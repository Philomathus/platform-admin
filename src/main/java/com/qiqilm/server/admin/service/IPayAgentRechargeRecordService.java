package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.PayAgentRechargeRecord;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IPayAgentRechargeRecordService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param orderNo 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayAgentRechargeRecord selectPayAgentRechargeRecordById(String orderNo);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payAgentRechargeRecord 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayAgentRechargeRecord> selectPayAgentRechargeRecordList(PayAgentRechargeRecord payAgentRechargeRecord);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payAgentRechargeRecord 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayAgentRechargeRecord(PayAgentRechargeRecord payAgentRechargeRecord);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payAgentRechargeRecord 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayAgentRechargeRecord(PayAgentRechargeRecord payAgentRechargeRecord);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param orderNos 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeRecordByIds(String[] orderNos );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param orderNo 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeRecordById(String orderNo);

	PayAgentRechargeRecord getCount(PayAgentRechargeRecord payAgentRechargeRecord);

	AjaxResult deposit(PayAgentRechargeRecord dto);

	AjaxResult proposed(PayAgentRechargeRecord dto);

}
