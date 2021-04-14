package com.qiqilm.server.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveHostPreMapper;
import com.qiqilm.server.admin.domain.LiveHostPre;
import com.qiqilm.server.admin.service.ILiveHostPreService;

/**
 * 主播开播时间预约Service业务层处理
 *
 * @author 77tv
 * @date 2021-04-13
 */
@Service
public class LiveHostPreServiceImpl implements ILiveHostPreService {
    @Autowired
    private LiveHostPreMapper liveHostPreMapper;

    /**
     * 查询主播开播时间预约
     *
     * @param id 主播开播时间预约ID
     * @return 主播开播时间预约
     */
    @Override
    public LiveHostPre selectLiveHostPreById(String id) {
        return liveHostPreMapper.selectLiveHostPreById(id);
    }

    /**
     * 查询主播开播时间预约列表
     *
     * @param liveHostPre 主播开播时间预约
     * @return 主播开播时间预约
     */
    @Override
    public List<LiveHostPre> selectLiveHostPreList(LiveHostPre liveHostPre) {
        if (liveHostPre.getLive() != null && liveHostPre.getLive().length > 0) {
            String liveI = null;
            for (int i = 0; i < liveHostPre.getLive().length; i++) {
                liveI = " and live_" + liveHostPre.getLive()[i] + " = 1 " + liveI;
            }
            liveI = liveI.substring(0, liveI.length() - 4);
            liveHostPre.setLiveI(liveI);
        }
        return liveHostPreMapper.selectLiveHostPreList(liveHostPre);
    }

    /**
     * 新增主播开播时间预约
     *
     * @param liveHostPre 主播开播时间预约
     * @return 结果
     */
    @Override
    public int insertLiveHostPre(LiveHostPre liveHostPre) {
        return liveHostPreMapper.insertLiveHostPre(liveHostPre);
    }

    /**
     * 修改主播开播时间预约
     *
     * @param liveHostPre 主播开播时间预约
     * @return 结果
     */
    @Override
    public int updateLiveHostPre(LiveHostPre liveHostPre) {
        return liveHostPreMapper.updateLiveHostPre(liveHostPre);
    }

    /**
     * 批量删除主播开播时间预约
     *
     * @param ids 需要删除的主播开播时间预约ID
     * @return 结果
     */
    @Override
    public int deleteLiveHostPreByIds(String[] ids) {
        return liveHostPreMapper.deleteLiveHostPreByIds(ids);
    }

    /**
     * 删除主播开播时间预约信息
     *
     * @param id 主播开播时间预约ID
     * @return 结果
     */
    @Override
    public int deleteLiveHostPreById(String id) {
        return liveHostPreMapper.deleteLiveHostPreById(id);
    }
}
