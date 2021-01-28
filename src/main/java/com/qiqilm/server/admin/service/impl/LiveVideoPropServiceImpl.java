package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveVideoPropMapper;
import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.service.ILiveVideoPropService;

/**
 * 送礼物Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveVideoPropServiceImpl implements ILiveVideoPropService {
    @Autowired
    private LiveVideoPropMapper liveVideoPropMapper;

    /**
     * 查询送礼物
     *
     * @param id 送礼物ID
     * @return 送礼物
     */
    @Override
    public LiveVideoProp selectLiveVideoPropById(Long id) {
        return liveVideoPropMapper.selectLiveVideoPropById(id);
    }

    /**
     * 查询送礼物列表
     *
     * @param liveVideoProp 送礼物
     * @return 送礼物
     */
    @Override
    public List<LiveVideoProp> selectLiveVideoPropList(LiveVideoProp liveVideoProp) {
        return liveVideoPropMapper.selectLiveVideoPropList(liveVideoProp);
    }

    /**
     * 新增送礼物
     *
     * @param liveVideoProp 送礼物
     * @return 结果
     */
    @Override
    public int insertLiveVideoProp(LiveVideoProp liveVideoProp) {
        liveVideoProp.setCreateTime(DateUtils.getNowDate());
        return liveVideoPropMapper.insertLiveVideoProp(liveVideoProp);
    }

    /**
     * 修改送礼物
     *
     * @param liveVideoProp 送礼物
     * @return 结果
     */
    @Override
    public int updateLiveVideoProp(LiveVideoProp liveVideoProp) {
        return liveVideoPropMapper.updateLiveVideoProp(liveVideoProp);
    }

    /**
     * 批量删除送礼物
     *
     * @param ids 需要删除的送礼物ID
     * @return 结果
     */
    @Override
    public int deleteLiveVideoPropByIds(Long[] ids) {
        return liveVideoPropMapper.deleteLiveVideoPropByIds(ids);
    }

    /**
     * 删除送礼物信息
     *
     * @param id 送礼物ID
     * @return 结果
     */
    @Override
    public int deleteLiveVideoPropById(Long id) {
        return liveVideoPropMapper.deleteLiveVideoPropById(id);
    }
}
