package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveVideoClassifiedMapper;
import com.qiqilm.server.admin.domain.LiveVideoClassified;
import com.qiqilm.server.admin.service.ILiveVideoClassifiedService;

/**
 * 分类Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class LiveVideoClassifiedServiceImpl implements ILiveVideoClassifiedService {
    @Autowired
    private LiveVideoClassifiedMapper liveVideoClassifiedMapper;

    /**
     * 查询分类
     *
     * @param id 分类ID
     * @return 分类
     */
    @Override
    public LiveVideoClassified selectLiveVideoClassifiedById(Long id) {
        return liveVideoClassifiedMapper.selectLiveVideoClassifiedById(id);
    }

    /**
     * 查询分类列表
     *
     * @param liveVideoClassified 分类
     * @return 分类
     */
    @Override
    public List<LiveVideoClassified> selectLiveVideoClassifiedList(LiveVideoClassified liveVideoClassified) {
        return liveVideoClassifiedMapper.selectLiveVideoClassifiedList(liveVideoClassified);
    }

    /**
     * 新增分类
     *
     * @param liveVideoClassified 分类
     * @return 结果
     */
    @Override
    public int insertLiveVideoClassified(LiveVideoClassified liveVideoClassified) {
        return liveVideoClassifiedMapper.insertLiveVideoClassified(liveVideoClassified);
    }

    /**
     * 修改分类
     *
     * @param liveVideoClassified 分类
     * @return 结果
     */
    @Override
    public int updateLiveVideoClassified(LiveVideoClassified liveVideoClassified) {
        return liveVideoClassifiedMapper.updateLiveVideoClassified(liveVideoClassified);
    }

    /**
     * 批量删除分类
     *
     * @param ids 需要删除的分类ID
     * @return 结果
     */
    @Override
    public int deleteLiveVideoClassifiedByIds(Long[] ids) {
        return liveVideoClassifiedMapper.deleteLiveVideoClassifiedByIds(ids);
    }

    /**
     * 删除分类信息
     *
     * @param id 分类ID
     * @return 结果
     */
    @Override
    public int deleteLiveVideoClassifiedById(Long id) {
        return liveVideoClassifiedMapper.deleteLiveVideoClassifiedById(id);
    }
}
