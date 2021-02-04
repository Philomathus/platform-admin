package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LivePayLog;

/**
 * //付费直播记录Service接口
 *
 * @author 77tv
 * @date 2021-02-03
 */
public interface ILivePayLogService {
	/**
	 * 查询//付费直播记录
	 *
	 * @param id //付费直播记录ID
	 * @return //付费直播记录
	 */
	public LivePayLog selectLivePayLogById(Long id);

	/**
	 * 查询//付费直播记录列表
	 *
	 * @param livePayLog //付费直播记录
	 * @return //付费直播记录集合
	 */
	public List<LivePayLog> selectLivePayLogList(LivePayLog livePayLog);

	/**
	 * 新增//付费直播记录
	 *
	 * @param livePayLog //付费直播记录
	 * @return 结果
	 */
	public int insertLivePayLog(LivePayLog livePayLog);

	/**
	 * 修改//付费直播记录
	 *
	 * @param livePayLog //付费直播记录
	 * @return 结果
	 */
	public int updateLivePayLog(LivePayLog livePayLog);

	/**
	 * 批量删除//付费直播记录
	 *
	 * @param ids 需要删除的//付费直播记录ID
	 * @return 结果
	 */
	public int deleteLivePayLogByIds(Long[] ids );

	/**
	 * 删除//付费直播记录信息
	 *
	 * @param id //付费直播记录ID
	 * @return 结果
	 */
	public int deleteLivePayLogById(Long id);
}
