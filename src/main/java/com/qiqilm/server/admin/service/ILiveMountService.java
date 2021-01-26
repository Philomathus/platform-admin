package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveMount;

/**
 * 礼物列Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ILiveMountService {
	/**
	 * 查询礼物列
	 *
	 * @param id 礼物列ID
	 * @return 礼物列
	 */
	public LiveMount selectLiveMountById(Long id);

	/**
	 * 查询礼物列列表
	 *
	 * @param liveMount 礼物列
	 * @return 礼物列集合
	 */
	public List<LiveMount> selectLiveMountList(LiveMount liveMount);

	/**
	 * 新增礼物列
	 *
	 * @param liveMount 礼物列
	 * @return 结果
	 */
	public int insertLiveMount(LiveMount liveMount);

	/**
	 * 修改礼物列
	 *
	 * @param liveMount 礼物列
	 * @return 结果
	 */
	public int updateLiveMount(LiveMount liveMount);

	/**
	 * 批量删除礼物列
	 *
	 * @param ids 需要删除的礼物列ID
	 * @return 结果
	 */
	public int deleteLiveMountByIds(Long[] ids );

	/**
	 * 删除礼物列信息
	 *
	 * @param id 礼物列ID
	 * @return 结果
	 */
	public int deleteLiveMountById(Long id);
}
