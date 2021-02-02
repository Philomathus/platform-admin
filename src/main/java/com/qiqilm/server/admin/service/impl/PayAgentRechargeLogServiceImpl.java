package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentRechargeLogMapper;
import com.qiqilm.server.admin.domain.PayAgentRechargeLog;
import com.qiqilm.server.admin.service.IPayAgentRechargeLogService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayAgentRechargeLogServiceImpl implements IPayAgentRechargeLogService {
    @Autowired
    private PayAgentRechargeLogMapper payAgentRechargeLogMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param orderNo 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayAgentRechargeLog selectPayAgentRechargeLogById(String orderNo) {
        return payAgentRechargeLogMapper.selectPayAgentRechargeLogById(orderNo);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payAgentRechargeLog 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayAgentRechargeLog> selectPayAgentRechargeLogList(PayAgentRechargeLog payAgentRechargeLog) {
        return payAgentRechargeLogMapper.selectPayAgentRechargeLogList(payAgentRechargeLog);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payAgentRechargeLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayAgentRechargeLog(PayAgentRechargeLog payAgentRechargeLog) {
        payAgentRechargeLog.setCreateTime(DateUtils.getNowDate());
        return payAgentRechargeLogMapper.insertPayAgentRechargeLog(payAgentRechargeLog);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payAgentRechargeLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayAgentRechargeLog(PayAgentRechargeLog payAgentRechargeLog) {
        return payAgentRechargeLogMapper.updatePayAgentRechargeLog(payAgentRechargeLog);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param orderNos 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeLogByIds(String[] orderNos) {
        return payAgentRechargeLogMapper.deletePayAgentRechargeLogByIds(orderNos);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param orderNo 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeLogById(String orderNo) {
        return payAgentRechargeLogMapper.deletePayAgentRechargeLogById(orderNo);
    }

    @Override
    public PayAgentRechargeLog count(PayAgentRechargeLog payAgentRechargeLog) {
        return payAgentRechargeLogMapper.count(payAgentRechargeLog);
    }
}
