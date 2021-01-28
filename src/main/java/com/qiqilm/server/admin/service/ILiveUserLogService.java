package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveUserLog;

/**
 * //帐户资金变动日志Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ILiveUserLogService {
	/**
	 * 查询//帐户资金变动日志
	 *
	 * @param id //帐户资金变动日志ID
	 * @return //帐户资金变动日志
	 */
	public LiveUserLog selectLiveUserLogById(Long id);

	/**
	 * 查询//帐户资金变动日志列表
	 *
	 * @param liveUserLog //帐户资金变动日志
	 * @return //帐户资金变动日志集合
	 */
	public List<LiveUserLog> selectLiveUserLogList(LiveUserLog liveUserLog);

	/**
	 * 新增//帐户资金变动日志
	 *
	 * @param liveUserLog //帐户资金变动日志
	 * @return 结果
	 */
	public int insertLiveUserLog(LiveUserLog liveUserLog);

	/**
	 * 修改//帐户资金变动日志
	 *
	 * @param liveUserLog //帐户资金变动日志
	 * @return 结果
	 */
	public int updateLiveUserLog(LiveUserLog liveUserLog);

	/**
	 * 批量删除//帐户资金变动日志
	 *
	 * @param ids 需要删除的//帐户资金变动日志ID
	 * @return 结果
	 */
	public int deleteLiveUserLogByIds(Long[] ids );

	/**
	 * 删除//帐户资金变动日志信息
	 *
	 * @param id //帐户资金变动日志ID
	 * @return 结果
	 */
	public int deleteLiveUserLogById(Long id);
}
