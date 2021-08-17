package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.PayAgentLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
	public PayAgentLog selectPayAgentLogById( Long id );

	/**
	 *  根据订单号代付平台
	 *
	 * @return 代付信息日志
	 */
	public PayAgentLog selectPayAgentLogByWithdrawOrderNo( String withdrawOrderNo );

	/**
	 * 查询代付信息日志列表
	 *
	 * @param payAgentLog 代付信息日志
	 * @return 代付信息日志集合
	 */
	public List<PayAgentLog> selectPayAgentLogList( PayAgentLog payAgentLog );

	/**
	 * 新增代付信息日志
	 *
	 * @param payAgentLog 代付信息日志
	 * @return 结果
	 */
	public int insertPayAgentLog( PayAgentLog payAgentLog );

	/**
	 * 修改代付信息日志
	 *
	 * @param payAgentLog 代付信息日志
	 * @return 结果
	 */
	public int updatePayAgentLog( PayAgentLog payAgentLog );

	/**
	 * 删除代付信息日志
	 *
	 * @param id 代付信息日志ID
	 * @return 结果
	 */
	public int deletePayAgentLogById( Long id );

	/**
	 * 批量删除代付信息日志
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayAgentLogByIds( Long[] ids );

	PayAgentLog selectByWithdrawOrderNo( String merOrderNo );

	PayAgentLog selectByPayAgentOrderNo( @Param( "orderCode" ) String orderCode );

	List<PayAgentLog> findNoCallback();

	int countNoFail( String merOrderNo );

	int countPlatOrderNo( @Param( "orderNo" ) String orderNo, @Param( "payAgentPlatId" ) Long payAgentPlatId );

    List<PayAgentLog> selectByAgentLogOrderList( @Param( "withdrawOrderNos" )List<String> withdrawOrderNos);

	PayAgentLog selectPayAgentLogOrderNo(String merOrderNo);
}
