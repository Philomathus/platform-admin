package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveVideo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
	 * 在线群组
	 * @return
	 */
	public List<String> selectOnlineLiveGroups();

	/**
	 * 修改直播
	 *
	 * @param liveVideo 直播
	 * @return 结果
	 */
	public int updateLiveVideo(LiveVideo liveVideo);

	public int updateLive7706Video(LiveVideo liveVideo);
	public int updateLive7705Video(LiveVideo liveVideo);
	public int updateLive7710Video(LiveVideo updateVideo);
	public int updateLive7711Video(LiveVideo updateVideo);
	public int updateLive77mmVideo(LiveVideo updateVideo);

	/**
	 * 修改直播,结束时间可以为null
	 *
	 * @param liveVideo 直播
	 * @return 结果
	 */
	public int updateLiveVideo2(LiveVideo liveVideo);

	LiveVideo selectLiveVideoSortById( Long id );

	List<LiveVideo> selectLiveInVideoSort();

	long countLiveInSort( Long sort );

	Integer countLineCount(@Param("paiId") Long paiId );

	List<LiveVideo> selectLiveInPlayDetect();

    List<LiveVideo> selectLiveVideoList2(LiveVideo liveVideo);

	List<Long> selectExpiredVideo();

	LiveVideo liveInStatus(Long userId);

	/**
	 * 新增直播
	 *
	 * @param liveVideo 直播
	 * @return 结果
	 */
	public int insertLiveVideo(LiveVideo liveVideo);
    void insertLiveVideo7706(LiveVideo liveVideo);
	void insertLiveVideo7705(LiveVideo liveVideo);
	void insertLiveVideo7710(LiveVideo liveVideo);
	void insertLiveVideo7711(LiveVideo liveVideo);
	void insertLiveVideo77mm(LiveVideo liveVideo);
}
