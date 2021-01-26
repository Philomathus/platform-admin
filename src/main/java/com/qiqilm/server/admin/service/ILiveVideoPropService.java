package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveVideoProp;

/**
 * 送礼物Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ILiveVideoPropService {
	/**
	 * 查询送礼物
	 *
	 * @param id 送礼物ID
	 * @return 送礼物
	 */
	public LiveVideoProp selectLiveVideoPropById(Long id);

	/**
	 * 查询送礼物列表
	 *
	 * @param liveVideoProp 送礼物
	 * @return 送礼物集合
	 */
	public List<LiveVideoProp> selectLiveVideoPropList(LiveVideoProp liveVideoProp);

	/**
	 * 新增送礼物
	 *
	 * @param liveVideoProp 送礼物
	 * @return 结果
	 */
	public int insertLiveVideoProp(LiveVideoProp liveVideoProp);

	/**
	 * 修改送礼物
	 *
	 * @param liveVideoProp 送礼物
	 * @return 结果
	 */
	public int updateLiveVideoProp(LiveVideoProp liveVideoProp);

	/**
	 * 批量删除送礼物
	 *
	 * @param ids 需要删除的送礼物ID
	 * @return 结果
	 */
	public int deleteLiveVideoPropByIds(Long[] ids );

	/**
	 * 删除送礼物信息
	 *
	 * @param id 送礼物ID
	 * @return 结果
	 */
	public int deleteLiveVideoPropById(Long id);
}
