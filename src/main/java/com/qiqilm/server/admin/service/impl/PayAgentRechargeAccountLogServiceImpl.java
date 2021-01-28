package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentRechargeAccountLogMapper;
import com.qiqilm.server.admin.domain.PayAgentRechargeAccountLog;
import com.qiqilm.server.admin.service.IPayAgentRechargeAccountLogService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayAgentRechargeAccountLogServiceImpl implements IPayAgentRechargeAccountLogService {
    @Autowired
    private PayAgentRechargeAccountLogMapper payAgentRechargeAccountLogMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param orderNo 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayAgentRechargeAccountLog selectPayAgentRechargeAccountLogById(String orderNo) {
        return payAgentRechargeAccountLogMapper.selectPayAgentRechargeAccountLogById(orderNo);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payAgentRechargeAccountLog 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayAgentRechargeAccountLog> selectPayAgentRechargeAccountLogList(PayAgentRechargeAccountLog payAgentRechargeAccountLog) {
        return payAgentRechargeAccountLogMapper.selectPayAgentRechargeAccountLogList(payAgentRechargeAccountLog);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payAgentRechargeAccountLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayAgentRechargeAccountLog(PayAgentRechargeAccountLog payAgentRechargeAccountLog) {
        payAgentRechargeAccountLog.setCreateTime(DateUtils.getNowDate());
        return payAgentRechargeAccountLogMapper.insertPayAgentRechargeAccountLog(payAgentRechargeAccountLog);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payAgentRechargeAccountLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayAgentRechargeAccountLog(PayAgentRechargeAccountLog payAgentRechargeAccountLog) {
        payAgentRechargeAccountLog.setUpdateTime(DateUtils.getNowDate());
        return payAgentRechargeAccountLogMapper.updatePayAgentRechargeAccountLog(payAgentRechargeAccountLog);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param orderNos 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeAccountLogByIds(String[] orderNos) {
        return payAgentRechargeAccountLogMapper.deletePayAgentRechargeAccountLogByIds(orderNos);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param orderNo 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeAccountLogById(String orderNo) {
        return payAgentRechargeAccountLogMapper.deletePayAgentRechargeAccountLogById(orderNo);
    }
}
