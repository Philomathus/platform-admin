package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveUserLogMapper;
import com.qiqilm.server.admin.domain.LiveUserLog;
import com.qiqilm.server.admin.service.ILiveUserLogService;

/**
 * //帐户资金变动日志Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveUserLogServiceImpl implements ILiveUserLogService {
    @Autowired
    private LiveUserLogMapper liveUserLogMapper;

    /**
     * 查询//帐户资金变动日志
     *
     * @param id //帐户资金变动日志ID
     * @return //帐户资金变动日志
     */
    @Override
    public LiveUserLog selectLiveUserLogById(Long id) {
        return liveUserLogMapper.selectLiveUserLogById(id);
    }

    /**
     * 查询//帐户资金变动日志列表
     *
     * @param liveUserLog //帐户资金变动日志
     * @return //帐户资金变动日志
     */
    @Override
    public List<LiveUserLog> selectLiveUserLogList(LiveUserLog liveUserLog) {
        return liveUserLogMapper.selectLiveUserLogList(liveUserLog);
    }

    /**
     * 新增//帐户资金变动日志
     *
     * @param liveUserLog //帐户资金变动日志
     * @return 结果
     */
    @Override
    public int insertLiveUserLog(LiveUserLog liveUserLog) {
        return liveUserLogMapper.insertLiveUserLog(liveUserLog);
    }

    /**
     * 修改//帐户资金变动日志
     *
     * @param liveUserLog //帐户资金变动日志
     * @return 结果
     */
    @Override
    public int updateLiveUserLog(LiveUserLog liveUserLog) {
        return liveUserLogMapper.updateLiveUserLog(liveUserLog);
    }

    /**
     * 批量删除//帐户资金变动日志
     *
     * @param ids 需要删除的//帐户资金变动日志ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserLogByIds(Long[] ids) {
        return liveUserLogMapper.deleteLiveUserLogByIds(ids);
    }

    /**
     * 删除//帐户资金变动日志信息
     *
     * @param id //帐户资金变动日志ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserLogById(Long id) {
        return liveUserLogMapper.deleteLiveUserLogById(id);
    }
}
