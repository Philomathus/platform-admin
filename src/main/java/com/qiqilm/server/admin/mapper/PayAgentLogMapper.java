package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentLog;

/**
 * 代付信息日志Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface PayAgentLogMapper {
	/**
	 * 查询代付信息日志
	 *
	 * @param id 代付信息日志ID
	 * @return 代付信息日志
	 */
	public PayAgentLog selectPayAgentLogById(Long id);

	/**
	 * 查询代付信息日志列表
	 *
	 * @param payAgentLog 代付信息日志
	 * @return 代付信息日志集合
	 */
	public List<PayAgentLog> selectPayAgentLogList(PayAgentLog payAgentLog);

	/**
	 * 新增代付信息日志
	 *
	 * @param payAgentLog 代付信息日志
	 * @return 结果
	 */
	public int insertPayAgentLog(PayAgentLog payAgentLog);

	/**
	 * 修改代付信息日志
	 *
	 * @param payAgentLog 代付信息日志
	 * @return 结果
	 */
	public int updatePayAgentLog(PayAgentLog payAgentLog);

	/**
	 * 删除代付信息日志
	 *
	 * @param id 代付信息日志ID
	 * @return 结果
	 */
	public int deletePayAgentLogById(Long id);

	/**
	 * 批量删除代付信息日志
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayAgentLogByIds(Long[] ids );

	PayAgentLog selectByWithdrawOrderNo( String merOrderNo );
}
