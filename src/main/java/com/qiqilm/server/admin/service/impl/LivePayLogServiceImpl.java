package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LivePayLogMapper;
import com.qiqilm.server.admin.domain.LivePayLog;
import com.qiqilm.server.admin.service.ILivePayLogService;

/**
 * //付费直播记录Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-03
 */
@Service
public class LivePayLogServiceImpl implements ILivePayLogService {
    @Autowired
    private LivePayLogMapper livePayLogMapper;

    /**
     * 查询//付费直播记录
     *
     * @param id //付费直播记录ID
     * @return //付费直播记录
     */
    @Override
    public LivePayLog selectLivePayLogById(Long id) {
        return livePayLogMapper.selectLivePayLogById(id);
    }

    /**
     * 查询//付费直播记录列表
     *
     * @param livePayLog //付费直播记录
     * @return //付费直播记录
     */
    @Override
    public List<LivePayLog> selectLivePayLogList(LivePayLog livePayLog) {
        return livePayLogMapper.selectLivePayLogList(livePayLog);
    }

    /**
     * 新增//付费直播记录
     *
     * @param livePayLog //付费直播记录
     * @return 结果
     */
    @Override
    public int insertLivePayLog(LivePayLog livePayLog) {
        livePayLog.setCreateTime(DateUtils.getNowDate());
        return livePayLogMapper.insertLivePayLog(livePayLog);
    }

    /**
     * 修改//付费直播记录
     *
     * @param livePayLog //付费直播记录
     * @return 结果
     */
    @Override
    public int updateLivePayLog(LivePayLog livePayLog) {
        return livePayLogMapper.updateLivePayLog(livePayLog);
    }

    /**
     * 批量删除//付费直播记录
     *
     * @param ids 需要删除的//付费直播记录ID
     * @return 结果
     */
    @Override
    public int deleteLivePayLogByIds(Long[] ids) {
        return livePayLogMapper.deleteLivePayLogByIds(ids);
    }

    /**
     * 删除//付费直播记录信息
     *
     * @param id //付费直播记录ID
     * @return 结果
     */
    @Override
    public int deleteLivePayLogById(Long id) {
        return livePayLogMapper.deleteLivePayLogById(id);
    }
}
