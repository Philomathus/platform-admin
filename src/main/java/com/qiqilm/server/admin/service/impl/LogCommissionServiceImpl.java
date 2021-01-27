package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LogCommissionMapper;
import com.qiqilm.server.admin.domain.LogCommission;
import com.qiqilm.server.admin.service.ILogCommissionService;

/**
 * 佣金领取日志Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class LogCommissionServiceImpl implements ILogCommissionService {
    @Autowired
    private LogCommissionMapper logCommissionMapper;

    /**
     * 查询佣金领取日志
     *
     * @param id 佣金领取日志ID
     * @return 佣金领取日志
     */
    @Override
    public LogCommission selectLogCommissionById(String id) {
        return logCommissionMapper.selectLogCommissionById(id);
    }

    /**
     * 查询佣金领取日志列表
     *
     * @param logCommission 佣金领取日志
     * @return 佣金领取日志
     */
    @Override
    public List<LogCommission> selectLogCommissionList(LogCommission logCommission) {
        return logCommissionMapper.selectLogCommissionList(logCommission);
    }

    /**
     * 新增佣金领取日志
     *
     * @param logCommission 佣金领取日志
     * @return 结果
     */
    @Override
    public int insertLogCommission(LogCommission logCommission) {
        logCommission.setCreateTime(DateUtils.getNowDate());
        return logCommissionMapper.insertLogCommission(logCommission);
    }

    /**
     * 修改佣金领取日志
     *
     * @param logCommission 佣金领取日志
     * @return 结果
     */
    @Override
    public int updateLogCommission(LogCommission logCommission) {
        return logCommissionMapper.updateLogCommission(logCommission);
    }

    /**
     * 批量删除佣金领取日志
     *
     * @param ids 需要删除的佣金领取日志ID
     * @return 结果
     */
    @Override
    public int deleteLogCommissionByIds(String[] ids) {
        return logCommissionMapper.deleteLogCommissionByIds(ids);
    }

    /**
     * 删除佣金领取日志信息
     *
     * @param id 佣金领取日志ID
     * @return 结果
     */
    @Override
    public int deleteLogCommissionById(String id) {
        return logCommissionMapper.deleteLogCommissionById(id);
    }
}
