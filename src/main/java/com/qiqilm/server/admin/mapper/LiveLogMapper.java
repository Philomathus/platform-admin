package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveLog;

/**
 * 系统日志Mapper接口
 *
 * @author 77tv
 * @date 2021-04-05
 */
public interface LiveLogMapper {
	/**
	 * 查询系统日志
	 *
	 * @param id 系统日志ID
	 * @return 系统日志
	 */
	public LiveLog selectLiveLogById(Long id);

	/**
	 * 查询系统日志列表
	 *
	 * @param liveLog 系统日志
	 * @return 系统日志集合
	 */
	public List<LiveLog> selectLiveLogList(LiveLog liveLog);

	/**
	 * 新增系统日志
	 *
	 * @param liveLog 系统日志
	 * @return 结果
	 */
	public int insertLiveLog(LiveLog liveLog);

	/**
	 * 修改系统日志
	 *
	 * @param liveLog 系统日志
	 * @return 结果
	 */
	public int updateLiveLog(LiveLog liveLog);

	/**
	 * 删除系统日志
	 *
	 * @param id 系统日志ID
	 * @return 结果
	 */
	public int deleteLiveLogById(Long id);

	/**
	 * 批量删除系统日志
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveLogByIds(Long[] ids );
}