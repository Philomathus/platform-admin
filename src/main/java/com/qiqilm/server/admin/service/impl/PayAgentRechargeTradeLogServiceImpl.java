package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentRechargeTradeLogMapper;
import com.qiqilm.server.admin.domain.PayAgentRechargeTradeLog;
import com.qiqilm.server.admin.service.IPayAgentRechargeTradeLogService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-01
 */
@Service
public class PayAgentRechargeTradeLogServiceImpl implements IPayAgentRechargeTradeLogService {
    @Autowired
    private PayAgentRechargeTradeLogMapper payAgentRechargeTradeLogMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param orderNo 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayAgentRechargeTradeLog selectPayAgentRechargeTradeLogById(String orderNo) {
        return payAgentRechargeTradeLogMapper.selectPayAgentRechargeTradeLogById(orderNo);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payAgentRechargeTradeLog 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayAgentRechargeTradeLog> selectPayAgentRechargeTradeLogList(PayAgentRechargeTradeLog payAgentRechargeTradeLog) {
        return payAgentRechargeTradeLogMapper.selectPayAgentRechargeTradeLogList(payAgentRechargeTradeLog);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payAgentRechargeTradeLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayAgentRechargeTradeLog(PayAgentRechargeTradeLog payAgentRechargeTradeLog) {
        payAgentRechargeTradeLog.setCreateTime(DateUtils.getNowDate());
        return payAgentRechargeTradeLogMapper.insertPayAgentRechargeTradeLog(payAgentRechargeTradeLog);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payAgentRechargeTradeLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayAgentRechargeTradeLog(PayAgentRechargeTradeLog payAgentRechargeTradeLog) {
        return payAgentRechargeTradeLogMapper.updatePayAgentRechargeTradeLog(payAgentRechargeTradeLog);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param orderNos 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeTradeLogByIds(String[] orderNos) {
        return payAgentRechargeTradeLogMapper.deletePayAgentRechargeTradeLogByIds(orderNos);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param orderNo 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeTradeLogById(String orderNo) {
        return payAgentRechargeTradeLogMapper.deletePayAgentRechargeTradeLogById(orderNo);
    }
}
