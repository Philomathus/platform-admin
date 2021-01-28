package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentLog;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IPayAgentLogService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayAgentLog selectPayAgentLogById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payAgentLog 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayAgentLog> selectPayAgentLogList(PayAgentLog payAgentLog);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payAgentLog 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayAgentLog(PayAgentLog payAgentLog);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payAgentLog 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayAgentLog(PayAgentLog payAgentLog);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentLogByIds(Long[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentLogById(Long id);
}
