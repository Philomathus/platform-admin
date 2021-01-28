package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentRechargeRecordMapper;
import com.qiqilm.server.admin.domain.PayAgentRechargeRecord;
import com.qiqilm.server.admin.service.IPayAgentRechargeRecordService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayAgentRechargeRecordServiceImpl implements IPayAgentRechargeRecordService {
    @Autowired
    private PayAgentRechargeRecordMapper payAgentRechargeRecordMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param orderNo 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayAgentRechargeRecord selectPayAgentRechargeRecordById(String orderNo) {
        return payAgentRechargeRecordMapper.selectPayAgentRechargeRecordById(orderNo);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payAgentRechargeRecord 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayAgentRechargeRecord> selectPayAgentRechargeRecordList(PayAgentRechargeRecord payAgentRechargeRecord) {
        return payAgentRechargeRecordMapper.selectPayAgentRechargeRecordList(payAgentRechargeRecord);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payAgentRechargeRecord 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayAgentRechargeRecord(PayAgentRechargeRecord payAgentRechargeRecord) {
        payAgentRechargeRecord.setCreateTime(DateUtils.getNowDate());
        return payAgentRechargeRecordMapper.insertPayAgentRechargeRecord(payAgentRechargeRecord);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payAgentRechargeRecord 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayAgentRechargeRecord(PayAgentRechargeRecord payAgentRechargeRecord) {
        return payAgentRechargeRecordMapper.updatePayAgentRechargeRecord(payAgentRechargeRecord);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param orderNos 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeRecordByIds(String[] orderNos) {
        return payAgentRechargeRecordMapper.deletePayAgentRechargeRecordByIds(orderNos);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param orderNo 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeRecordById(String orderNo) {
        return payAgentRechargeRecordMapper.deletePayAgentRechargeRecordById(orderNo);
    }
}
