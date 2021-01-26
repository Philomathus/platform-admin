package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveGuardConfigMapper;
import com.qiqilm.server.admin.domain.LiveGuardConfig;
import com.qiqilm.server.admin.service.ILiveGuardConfigService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveGuardConfigServiceImpl implements ILiveGuardConfigService {
    @Autowired
    private LiveGuardConfigMapper liveGuardConfigMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LiveGuardConfig selectLiveGuardConfigById(Long id) {
        return liveGuardConfigMapper.selectLiveGuardConfigById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param liveGuardConfig 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<LiveGuardConfig> selectLiveGuardConfigList(LiveGuardConfig liveGuardConfig) {
        return liveGuardConfigMapper.selectLiveGuardConfigList(liveGuardConfig);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param liveGuardConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertLiveGuardConfig(LiveGuardConfig liveGuardConfig) {
        return liveGuardConfigMapper.insertLiveGuardConfig(liveGuardConfig);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param liveGuardConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateLiveGuardConfig(LiveGuardConfig liveGuardConfig) {
        return liveGuardConfigMapper.updateLiveGuardConfig(liveGuardConfig);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveGuardConfigByIds(Long[] ids) {
        return liveGuardConfigMapper.deleteLiveGuardConfigByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveGuardConfigById(Long id) {
        return liveGuardConfigMapper.deleteLiveGuardConfigById(id);
    }
}
