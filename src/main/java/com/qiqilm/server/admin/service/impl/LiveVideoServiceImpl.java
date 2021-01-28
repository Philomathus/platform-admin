package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveVideoMapper;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.service.ILiveVideoService;

/**
 * 直播Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class LiveVideoServiceImpl implements ILiveVideoService {
    @Autowired
    private LiveVideoMapper liveVideoMapper;

    /**
     * 查询直播
     *
     * @param id 直播ID
     * @return 直播
     */
    @Override
    public LiveVideo selectLiveVideoById(Long id) {
        return liveVideoMapper.selectLiveVideoById(id);
    }

    /**
     * 查询直播列表
     *
     * @param liveVideo 直播
     * @return 直播
     */
    @Override
    public List<LiveVideo> selectLiveVideoList(LiveVideo liveVideo) {
        return liveVideoMapper.selectLiveVideoList(liveVideo);
    }

    /**
     * 新增直播
     *
     * @param liveVideo 直播
     * @return 结果
     */
    @Override
    public int insertLiveVideo(LiveVideo liveVideo) {
        liveVideo.setCreateTime(DateUtils.getNowDate());
        return liveVideoMapper.insertLiveVideo(liveVideo);
    }

    /**
     * 修改直播
     *
     * @param liveVideo 直播
     * @return 结果
     */
    @Override
    public int updateLiveVideo(LiveVideo liveVideo) {
        return liveVideoMapper.updateLiveVideo(liveVideo);
    }

    /**
     * 批量删除直播
     *
     * @param ids 需要删除的直播ID
     * @return 结果
     */
    @Override
    public int deleteLiveVideoByIds(Long[] ids) {
        return liveVideoMapper.deleteLiveVideoByIds(ids);
    }

    /**
     * 删除直播信息
     *
     * @param id 直播ID
     * @return 结果
     */
    @Override
    public int deleteLiveVideoById(Long id) {
        return liveVideoMapper.deleteLiveVideoById(id);
    }
}
