package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveVideo;

/**
 * 直播Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface ILiveVideoService {
	/**
	 * 查询直播
	 *
	 * @param id 直播ID
	 * @return 直播
	 */
	public LiveVideo selectLiveVideoById(Long id);

	/**
	 * 查询直播列表
	 *
	 * @param liveVideo 直播
	 * @return 直播集合
	 */
	public List<LiveVideo> selectLiveVideoList(LiveVideo liveVideo);

	/**
	 * 新增直播
	 *
	 * @param liveVideo 直播
	 * @return 结果
	 */
	public int insertLiveVideo(LiveVideo liveVideo);

	/**
	 * 修改直播
	 *
	 * @param liveVideo 直播
	 * @return 结果
	 */
	public int updateLiveVideo(LiveVideo liveVideo);

	/**
	 * 批量删除直播
	 *
	 * @param ids 需要删除的直播ID
	 * @return 结果
	 */
	public int deleteLiveVideoByIds(Long[] ids );

	/**
	 * 删除直播信息
	 *
	 * @param id 直播ID
	 * @return 结果
	 */
	public int deleteLiveVideoById(Long id);

	public boolean close( Long id, String cause );
}
