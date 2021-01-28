package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentRechargeAccountMapper;
import com.qiqilm.server.admin.domain.PayAgentRechargeAccount;
import com.qiqilm.server.admin.service.IPayAgentRechargeAccountService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayAgentRechargeAccountServiceImpl implements IPayAgentRechargeAccountService {
    @Autowired
    private PayAgentRechargeAccountMapper payAgentRechargeAccountMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayAgentRechargeAccount selectPayAgentRechargeAccountById(Long id) {
        return payAgentRechargeAccountMapper.selectPayAgentRechargeAccountById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payAgentRechargeAccount 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayAgentRechargeAccount> selectPayAgentRechargeAccountList(PayAgentRechargeAccount payAgentRechargeAccount) {
        return payAgentRechargeAccountMapper.selectPayAgentRechargeAccountList(payAgentRechargeAccount);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payAgentRechargeAccount 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayAgentRechargeAccount(PayAgentRechargeAccount payAgentRechargeAccount) {
        payAgentRechargeAccount.setCreateTime(DateUtils.getNowDate());
        return payAgentRechargeAccountMapper.insertPayAgentRechargeAccount(payAgentRechargeAccount);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payAgentRechargeAccount 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayAgentRechargeAccount(PayAgentRechargeAccount payAgentRechargeAccount) {
        return payAgentRechargeAccountMapper.updatePayAgentRechargeAccount(payAgentRechargeAccount);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeAccountByIds(Long[] ids) {
        return payAgentRechargeAccountMapper.deletePayAgentRechargeAccountByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeAccountById(Long id) {
        return payAgentRechargeAccountMapper.deletePayAgentRechargeAccountById(id);
    }
}
