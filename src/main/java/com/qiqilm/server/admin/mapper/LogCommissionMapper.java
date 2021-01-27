package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LogCommission;

/**
 * 佣金领取日志Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface LogCommissionMapper {
	/**
	 * 查询佣金领取日志
	 *
	 * @param id 佣金领取日志ID
	 * @return 佣金领取日志
	 */
	public LogCommission selectLogCommissionById(String id);

	/**
	 * 查询佣金领取日志列表
	 *
	 * @param logCommission 佣金领取日志
	 * @return 佣金领取日志集合
	 */
	public List<LogCommission> selectLogCommissionList(LogCommission logCommission);

	/**
	 * 新增佣金领取日志
	 *
	 * @param logCommission 佣金领取日志
	 * @return 结果
	 */
	public int insertLogCommission(LogCommission logCommission);

	/**
	 * 修改佣金领取日志
	 *
	 * @param logCommission 佣金领取日志
	 * @return 结果
	 */
	public int updateLogCommission(LogCommission logCommission);

	/**
	 * 删除佣金领取日志
	 *
	 * @param id 佣金领取日志ID
	 * @return 结果
	 */
	public int deleteLogCommissionById(String id);

	/**
	 * 批量删除佣金领取日志
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLogCommissionByIds(String[] ids );
}
