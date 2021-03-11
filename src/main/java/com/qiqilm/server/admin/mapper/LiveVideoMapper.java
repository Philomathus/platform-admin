package com.qiqilm.server.admin.mapper;

import java.util.List;
import java.util.Map;

import com.qiqilm.server.admin.domain.LiveVideo;

/**
 * 直播Mapper接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface LiveVideoMapper {
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
	 * 修改直播
	 *
	 * @param liveVideo 直播
	 * @return 结果
	 */
	public int updateLiveVideo(LiveVideo liveVideo);

	LiveVideo selectLiveVideoSortById( Long id );

	List<LiveVideo> selectLiveInVideoSort();

	long countLiveInSort( Long sort );
}
