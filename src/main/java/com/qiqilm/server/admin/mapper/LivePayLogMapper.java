package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LivePayLog;

/**
 * 付费直播记录Mapper接口
 *
 * @author 77tv
 * @date 2021-02-03
 */
public interface LivePayLogMapper {
	/**
	 * 查询付费直播记录
	 *
	 * @param id 付费直播记录ID
	 * @return 付费直播记录
	 */
	public LivePayLog selectLivePayLogById(Long id);

	/**
	 * 查询付费直播记录列表
	 *
	 * @param livePayLog 付费直播记录
	 * @return 付费直播记录集合
	 */
	public List<LivePayLog> selectLivePayLogList(LivePayLog livePayLog);

	/**
	 * 新增付费直播记录
	 *
	 * @param livePayLog 付费直播记录
	 * @return 结果
	 */
	public int insertLivePayLog(LivePayLog livePayLog);

	/**
	 * 修改付费直播记录
	 *
	 * @param livePayLog 付费直播记录
	 * @return 结果
	 */
	public int updateLivePayLog(LivePayLog livePayLog);
}
