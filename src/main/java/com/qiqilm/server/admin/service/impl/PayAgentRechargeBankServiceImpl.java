package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentRechargeBankMapper;
import com.qiqilm.server.admin.domain.PayAgentRechargeBank;
import com.qiqilm.server.admin.service.IPayAgentRechargeBankService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayAgentRechargeBankServiceImpl implements IPayAgentRechargeBankService {
    @Autowired
    private PayAgentRechargeBankMapper payAgentRechargeBankMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayAgentRechargeBank selectPayAgentRechargeBankById(Long id) {
        return payAgentRechargeBankMapper.selectPayAgentRechargeBankById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payAgentRechargeBank 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayAgentRechargeBank> selectPayAgentRechargeBankList(PayAgentRechargeBank payAgentRechargeBank) {
        return payAgentRechargeBankMapper.selectPayAgentRechargeBankList(payAgentRechargeBank);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payAgentRechargeBank 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayAgentRechargeBank(PayAgentRechargeBank payAgentRechargeBank) {
        payAgentRechargeBank.setCreateTime(DateUtils.getNowDate());
        return payAgentRechargeBankMapper.insertPayAgentRechargeBank(payAgentRechargeBank);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payAgentRechargeBank 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayAgentRechargeBank(PayAgentRechargeBank payAgentRechargeBank) {
        payAgentRechargeBank.setUpdateTime(DateUtils.getNowDate());
        return payAgentRechargeBankMapper.updatePayAgentRechargeBank(payAgentRechargeBank);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeBankByIds(Long[] ids) {
        return payAgentRechargeBankMapper.deletePayAgentRechargeBankByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeBankById(Long id) {
        return payAgentRechargeBankMapper.deletePayAgentRechargeBankById(id);
    }
}
