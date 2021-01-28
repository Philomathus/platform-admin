package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveFamily;

/**
 * 家族Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface ILiveFamilyService {
	/**
	 * 查询家族
	 *
	 * @param id 家族ID
	 * @return 家族
	 */
	public LiveFamily selectLiveFamilyById(Long id);

	/**
	 * 查询家族列表
	 *
	 * @param liveFamily 家族
	 * @return 家族集合
	 */
	public List<LiveFamily> selectLiveFamilyList(LiveFamily liveFamily);

	/**
	 * 新增家族
	 *
	 * @param liveFamily 家族
	 * @return 结果
	 */
	public int insertLiveFamily(LiveFamily liveFamily);

	/**
	 * 修改家族
	 *
	 * @param liveFamily 家族
	 * @return 结果
	 */
	public int updateLiveFamily(LiveFamily liveFamily);

	/**
	 * 批量删除家族
	 *
	 * @param ids 需要删除的家族ID
	 * @return 结果
	 */
	public int deleteLiveFamilyByIds(Long[] ids );

	/**
	 * 删除家族信息
	 *
	 * @param id 家族ID
	 * @return 结果
	 */
	public int deleteLiveFamilyById(Long id);
}
