package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveComplaint;

/**
 * 主播投诉记录Service接口
 *
 * @author 77tv
 * @date 2021-08-14
 */
public interface ILiveComplaintService {
	/**
	 * 查询主播投诉记录
	 *
	 * @param id 主播投诉记录ID
	 * @return 主播投诉记录
	 */
//	public LiveComplaint selectLiveComplaintById(Long id);

	/**
	 * 查询主播投诉记录列表
	 *
	 * @param liveComplaint 主播投诉记录
	 * @return 主播投诉记录集合
	 */
	public List<LiveComplaint> selectLiveComplaintList(LiveComplaint liveComplaint);

	/**
	 * 新增主播投诉记录
	 *
	 * @param liveComplaint 主播投诉记录
	 * @return 结果
	 */
//	public int insertLiveComplaint(LiveComplaint liveComplaint);

	/**
	 * 修改主播投诉记录
	 *
	 * @param liveComplaint 主播投诉记录
	 * @return 结果
	 */
//	public int updateLiveComplaint(LiveComplaint liveComplaint);

	/**
	 * 批量删除主播投诉记录
	 *
	 * @param ids 需要删除的主播投诉记录ID
	 * @return 结果
	 */
//	public int deleteLiveComplaintByIds(Long[] ids );

	/**
	 * 删除主播投诉记录信息
	 *
	 * @param id 主播投诉记录ID
	 * @return 结果
	 */
//	public int deleteLiveComplaintById(Long id);
}
