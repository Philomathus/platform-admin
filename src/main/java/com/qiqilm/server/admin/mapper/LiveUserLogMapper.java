package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveUserLog;

/**
 * //帐户资金变动日志Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface LiveUserLogMapper {
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
	 * 删除//帐户资金变动日志
	 *
	 * @param id //帐户资金变动日志ID
	 * @return 结果
	 */
	public int deleteLiveUserLogById(Long id);

	/**
	 * 批量删除//帐户资金变动日志
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveUserLogByIds(Long[] ids );
}
