package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveVideoClassified;

/**
 * 分类Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface ILiveVideoClassifiedService {
	/**
	 * 查询分类
	 *
	 * @param id 分类ID
	 * @return 分类
	 */
	public LiveVideoClassified selectLiveVideoClassifiedById(Long id);

	/**
	 * 查询分类列表
	 *
	 * @param liveVideoClassified 分类
	 * @return 分类集合
	 */
	public List<LiveVideoClassified> selectLiveVideoClassifiedList(LiveVideoClassified liveVideoClassified);

	/**
	 * 新增分类
	 *
	 * @param liveVideoClassified 分类
	 * @return 结果
	 */
	public int insertLiveVideoClassified(LiveVideoClassified liveVideoClassified);

	/**
	 * 修改分类
	 *
	 * @param liveVideoClassified 分类
	 * @return 结果
	 */
	public int updateLiveVideoClassified(LiveVideoClassified liveVideoClassified);

	/**
	 * 批量删除分类
	 *
	 * @param ids 需要删除的分类ID
	 * @return 结果
	 */
	public int deleteLiveVideoClassifiedByIds(Long[] ids );

	/**
	 * 删除分类信息
	 *
	 * @param id 分类ID
	 * @return 结果
	 */
	public int deleteLiveVideoClassifiedById(Long id);
}
