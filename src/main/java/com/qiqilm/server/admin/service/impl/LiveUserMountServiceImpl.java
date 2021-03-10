package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveUserMountMapper;
import com.qiqilm.server.admin.domain.LiveUserMount;
import com.qiqilm.server.admin.service.ILiveUserMountService;

/**
 * 直播间会员坐骑Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-09
 */
@Service
public class LiveUserMountServiceImpl implements ILiveUserMountService {
    @Autowired
    private LiveUserMountMapper liveUserMountMapper;

    /**
     * 查询直播间会员坐骑
     *
     * @param id 直播间会员坐骑ID
     * @return 直播间会员坐骑
     */
    @Override
    public LiveUserMount selectLiveUserMountById(Long id) {
        return liveUserMountMapper.selectLiveUserMountById(id);
    }

    /**
     * 查询直播间会员坐骑列表
     *
     * @param liveUserMount 直播间会员坐骑
     * @return 直播间会员坐骑
     */
    @Override
    public List<LiveUserMount> selectLiveUserMountList(LiveUserMount liveUserMount) {
        return liveUserMountMapper.selectLiveUserMountList(liveUserMount);
    }

    /**
     * 新增直播间会员坐骑
     *
     * @param liveUserMount 直播间会员坐骑
     * @return 结果
     */
    @Override
    public int insertLiveUserMount(LiveUserMount liveUserMount) {
        return liveUserMountMapper.insertLiveUserMount(liveUserMount);
    }

    /**
     * 修改直播间会员坐骑
     *
     * @param liveUserMount 直播间会员坐骑
     * @return 结果
     */
    @Override
    public int updateLiveUserMount(LiveUserMount liveUserMount) {
        return liveUserMountMapper.updateLiveUserMount(liveUserMount);
    }

    /**
     * 批量删除直播间会员坐骑
     *
     * @param ids 需要删除的直播间会员坐骑ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserMountByIds(Long[] ids) {
        return liveUserMountMapper.deleteLiveUserMountByIds(ids);
    }

    /**
     * 删除直播间会员坐骑信息
     *
     * @param id 直播间会员坐骑ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserMountById(Long id) {
        return liveUserMountMapper.deleteLiveUserMountById(id);
    }
}
