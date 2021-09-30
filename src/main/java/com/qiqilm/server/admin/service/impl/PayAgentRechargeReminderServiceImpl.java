package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentRechargeReminderMapper;
import com.qiqilm.server.admin.domain.PayAgentRechargeReminder;
import com.qiqilm.server.admin.service.IPayAgentRechargeReminderService;

/**
 * 代充银行提示语Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-25
 */
@Service
public class PayAgentRechargeReminderServiceImpl implements IPayAgentRechargeReminderService {
    @Autowired
    private PayAgentRechargeReminderMapper payAgentRechargeReminderMapper;

    /**
     * 查询代充银行提示语
     *
     * @param id 代充银行提示语ID
     * @return 代充银行提示语
     */
    @Override
    public PayAgentRechargeReminder selectPayAgentRechargeReminderById(Long id) {
        return payAgentRechargeReminderMapper.selectPayAgentRechargeReminderById(id);
    }

    /**
     * 查询代充银行提示语列表
     *
     * @param payAgentRechargeReminder 代充银行提示语
     * @return 代充银行提示语
     */
    @Override
    public List<PayAgentRechargeReminder> selectPayAgentRechargeReminderList(PayAgentRechargeReminder payAgentRechargeReminder) {
        return payAgentRechargeReminderMapper.selectPayAgentRechargeReminderList(payAgentRechargeReminder);
    }

    /**
     * 新增代充银行提示语
     *
     * @param payAgentRechargeReminder 代充银行提示语
     * @return 结果
     */
    @Override
    public int insertPayAgentRechargeReminder(PayAgentRechargeReminder payAgentRechargeReminder) {
        payAgentRechargeReminder.setCreateTime(DateUtils.getNowDate());
        return payAgentRechargeReminderMapper.insertPayAgentRechargeReminder(payAgentRechargeReminder);
    }

    /**
     * 修改代充银行提示语
     *
     * @param payAgentRechargeReminder 代充银行提示语
     * @return 结果
     */
    @Override
    public int updatePayAgentRechargeReminder(PayAgentRechargeReminder payAgentRechargeReminder) {
        payAgentRechargeReminder.setUpdateTime(DateUtils.getNowDate());
        return payAgentRechargeReminderMapper.updatePayAgentRechargeReminder(payAgentRechargeReminder);
    }

    /**
     * 批量删除代充银行提示语
     *
     * @param ids 需要删除的代充银行提示语ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeReminderByIds(Long[] ids) {
        return payAgentRechargeReminderMapper.deletePayAgentRechargeReminderByIds(ids);
    }

    /**
     * 删除代充银行提示语信息
     *
     * @param id 代充银行提示语ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeReminderById(Long id) {
        return payAgentRechargeReminderMapper.deletePayAgentRechargeReminderById(id);
    }
}
