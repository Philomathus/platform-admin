package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveUserWithdrawNewlogMapper;
import com.qiqilm.server.admin.domain.LiveUserWithdrawNewlog;
import com.qiqilm.server.admin.service.ILiveUserWithdrawNewlogService;

/**
 * 主播提现管理Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-23
 */
@Service
public class LiveUserWithdrawNewlogServiceImpl implements ILiveUserWithdrawNewlogService {
    @Autowired
    private LiveUserWithdrawNewlogMapper liveUserWithdrawNewlogMapper;

    /**
     * 查询主播提现管理
     *
     * @param id 主播提现管理ID
     * @return 主播提现管理
     */
    @Override
    public LiveUserWithdrawNewlog selectLiveUserWithdrawNewlogById(String id) {
        return liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(id);
    }

    /**
     * 查询主播提现管理列表
     *
     * @param liveUserWithdrawNewlog 主播提现管理
     * @return 主播提现管理
     */
    @Override
    public List<LiveUserWithdrawNewlog> selectLiveUserWithdrawNewlogList(LiveUserWithdrawNewlog liveUserWithdrawNewlog) {
        return liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogList(liveUserWithdrawNewlog);
    }

    /**
     * 新增主播提现管理
     *
     * @param liveUserWithdrawNewlog 主播提现管理
     * @return 结果
     */
    @Override
    public int insertLiveUserWithdrawNewlog(LiveUserWithdrawNewlog liveUserWithdrawNewlog) {
        liveUserWithdrawNewlog.setCreateTime(DateUtils.getNowDate());
        return liveUserWithdrawNewlogMapper.insertLiveUserWithdrawNewlog(liveUserWithdrawNewlog);
    }

    /**
     * 修改主播提现管理
     *
     * @param liveUserWithdrawNewlog 主播提现管理
     * @return 结果
     */
    @Override
    public int updateLiveUserWithdrawNewlog(LiveUserWithdrawNewlog liveUserWithdrawNewlog) {
        liveUserWithdrawNewlog.setUpdateTime(DateUtils.getNowDate());
        return liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(liveUserWithdrawNewlog);
    }

    /**
     * 批量删除主播提现管理
     *
     * @param ids 需要删除的主播提现管理ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserWithdrawNewlogByIds(String[] ids) {
        return liveUserWithdrawNewlogMapper.deleteLiveUserWithdrawNewlogByIds(ids);
    }

    /**
     * 删除主播提现管理信息
     *
     * @param id 主播提现管理ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserWithdrawNewlogById(String id) {
        return liveUserWithdrawNewlogMapper.deleteLiveUserWithdrawNewlogById(id);
    }
}
