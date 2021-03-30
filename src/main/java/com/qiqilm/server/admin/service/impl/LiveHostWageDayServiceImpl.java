package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveHostWageDayMapper;
import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.service.ILiveHostWageDayService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-29
 */
@Service
public class LiveHostWageDayServiceImpl implements ILiveHostWageDayService {
    @Autowired
    private LiveHostWageDayMapper liveHostWageDayMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LiveHostWageDay selectLiveHostWageDayById(String id) {
        return liveHostWageDayMapper.selectLiveHostWageDayById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param liveHostWageDay 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<LiveHostWageDay> selectLiveHostWageDayList(LiveHostWageDay liveHostWageDay) {
        return liveHostWageDayMapper.selectLiveHostWageDayList(liveHostWageDay);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param liveHostWageDay 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertLiveHostWageDay(LiveHostWageDay liveHostWageDay) {
        return liveHostWageDayMapper.insertLiveHostWageDay(liveHostWageDay);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param liveHostWageDay 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateLiveHostWageDay(LiveHostWageDay liveHostWageDay) {
        return liveHostWageDayMapper.updateLiveHostWageDay(liveHostWageDay);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveHostWageDayByIds(String[] ids) {
        return liveHostWageDayMapper.deleteLiveHostWageDayByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveHostWageDayById(String id) {
        return liveHostWageDayMapper.deleteLiveHostWageDayById(id);
    }
}