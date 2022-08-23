package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveVideo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

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
     *
     * @return
     */
    public List<String> selectOnlineLiveGroups();

    /**
     * 修改直播
     *
     * @param liveVideo 直播
     * @return 结果
     */
    public int updateLiveVideo(@Param("lv") LiveVideo liveVideo, @Param("dbMainLive") String dbMainLive);

    /**
     * 修改直播,结束时间可以为null
     *
     * @param liveVideo 直播
     * @return 结果
     */
    public int updateLiveVideo2(LiveVideo liveVideo);

    LiveVideo selectLiveVideoSortById(@Param("id") Long id, @Param("dbMainLive") String liveSubAgentDbLive);

    List<LiveVideo> selectLiveInVideoSort();

    long countLiveInSort(Long sort);

    Integer countLineCount(@Param("paiId") Long paiId);

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
    public int insertLiveVideo(@Param("lv") LiveVideo liveVideo, @Param("dbMainLive") String dbMainLive);

    List<LiveVideo> selectLiveVideoInIds(@Param("array") Set<Long> ids);

    List<LiveVideo> selectMainLiveInVideoList(@Param("dbMainLive") String liveCenterDbLive);

    Integer countLiveVideo(Long id);

    int updateLiveVideoSort(LiveVideo liveVideo);
}
