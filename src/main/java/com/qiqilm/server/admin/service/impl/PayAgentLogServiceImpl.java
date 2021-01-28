package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentLogMapper;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.service.IPayAgentLogService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayAgentLogServiceImpl implements IPayAgentLogService {
    @Autowired
    private PayAgentLogMapper payAgentLogMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayAgentLog selectPayAgentLogById(Long id) {
        return payAgentLogMapper.selectPayAgentLogById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payAgentLog 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayAgentLog> selectPayAgentLogList(PayAgentLog payAgentLog) {
        return payAgentLogMapper.selectPayAgentLogList(payAgentLog);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payAgentLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayAgentLog(PayAgentLog payAgentLog) {
        payAgentLog.setCreateTime(DateUtils.getNowDate());
        return payAgentLogMapper.insertPayAgentLog(payAgentLog);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payAgentLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayAgentLog(PayAgentLog payAgentLog) {
        return payAgentLogMapper.updatePayAgentLog(payAgentLog);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentLogByIds(Long[] ids) {
        return payAgentLogMapper.deletePayAgentLogByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentLogById(Long id) {
        return payAgentLogMapper.deletePayAgentLogById(id);
    }
}
