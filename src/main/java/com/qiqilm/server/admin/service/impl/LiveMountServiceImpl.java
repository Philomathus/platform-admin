package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveMountMapper;
import com.qiqilm.server.admin.domain.LiveMount;
import com.qiqilm.server.admin.service.ILiveMountService;

/**
 * 礼物列Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveMountServiceImpl implements ILiveMountService {
    @Autowired
    private LiveMountMapper liveMountMapper;

    /**
     * 查询礼物列
     *
     * @param id 礼物列ID
     * @return 礼物列
     */
    @Override
    public LiveMount selectLiveMountById(Long id) {
        return liveMountMapper.selectLiveMountById(id);
    }

    /**
     * 查询礼物列列表
     *
     * @param liveMount 礼物列
     * @return 礼物列
     */
    @Override
    public List<LiveMount> selectLiveMountList(LiveMount liveMount) {
        return liveMountMapper.selectLiveMountList(liveMount);
    }

    /**
     * 新增礼物列
     *
     * @param liveMount 礼物列
     * @return 结果
     */
    @Override
    public int insertLiveMount(LiveMount liveMount) {
        return liveMountMapper.insertLiveMount(liveMount);
    }

    /**
     * 修改礼物列
     *
     * @param liveMount 礼物列
     * @return 结果
     */
    @Override
    public int updateLiveMount(LiveMount liveMount) {
        return liveMountMapper.updateLiveMount(liveMount);
    }

    /**
     * 批量删除礼物列
     *
     * @param ids 需要删除的礼物列ID
     * @return 结果
     */
    @Override
    public int deleteLiveMountByIds(Long[] ids) {
        return liveMountMapper.deleteLiveMountByIds(ids);
    }

    /**
     * 删除礼物列信息
     *
     * @param id 礼物列ID
     * @return 结果
     */
    @Override
    public int deleteLiveMountById(Long id) {
        return liveMountMapper.deleteLiveMountById(id);
    }
}
