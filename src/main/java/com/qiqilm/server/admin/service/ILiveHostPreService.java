package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveHostPre;

/**
 * 主播开播时间预约Service接口
 *
 * @author 77tv
 * @date 2021-04-13
 */
public interface ILiveHostPreService {
	/**
	 * 查询主播开播时间预约
	 *
	 * @param id 主播开播时间预约ID
	 * @return 主播开播时间预约
	 */
	public LiveHostPre selectLiveHostPreById(String id);

	/**
	 * 查询主播开播时间预约列表
	 *
	 * @param liveHostPre 主播开播时间预约
	 * @return 主播开播时间预约集合
	 */
	public List<LiveHostPre> selectLiveHostPreList(LiveHostPre liveHostPre);

	/**
	 * 新增主播开播时间预约
	 *
	 * @param liveHostPre 主播开播时间预约
	 * @return 结果
	 */
	public int insertLiveHostPre(LiveHostPre liveHostPre);

	/**
	 * 修改主播开播时间预约
	 *
	 * @param liveHostPre 主播开播时间预约
	 * @return 结果
	 */
	public int updateLiveHostPre(LiveHostPre liveHostPre);

	/**
	 * 批量删除主播开播时间预约
	 *
	 * @param ids 需要删除的主播开播时间预约ID
	 * @return 结果
	 */
	public int deleteLiveHostPreByIds(String[] ids );

	/**
	 * 删除主播开播时间预约信息
	 *
	 * @param id 主播开播时间预约ID
	 * @return 结果
	 */
	public int deleteLiveHostPreById(String id);
}
