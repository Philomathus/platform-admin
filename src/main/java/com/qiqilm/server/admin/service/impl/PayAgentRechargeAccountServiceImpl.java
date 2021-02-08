package com.qiqilm.server.admin.service.impl;

import java.math.BigDecimal;
import java.util.List;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentRechargeAccountMapper;
import com.qiqilm.server.admin.domain.PayAgentRechargeAccount;
import com.qiqilm.server.admin.service.IPayAgentRechargeAccountService;

/**
 * 【代充人】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayAgentRechargeAccountServiceImpl implements IPayAgentRechargeAccountService {
    @Autowired
    private PayAgentRechargeAccountMapper payAgentRechargeAccountMapper;
    @Autowired
    private MemberInfoMapper memberInfoMapper;

    /**
     * 查询【代充人】
     *
     * @param id 【代充人】ID
     * @return 【代充人】
     */
    @Override
    public PayAgentRechargeAccount selectPayAgentRechargeAccountById(Long id) {
        return payAgentRechargeAccountMapper.selectPayAgentRechargeAccountById(id);
    }

    /**
     * 查询【代充人】列表
     *
     * @param payAgentRechargeAccount 【代充人】
     * @return 【代充人】
     */
    @Override
    public List<PayAgentRechargeAccount> selectPayAgentRechargeAccountList(PayAgentRechargeAccount payAgentRechargeAccount) {
        return payAgentRechargeAccountMapper.selectPayAgentRechargeAccountList(payAgentRechargeAccount);
    }

    /**
     * 新增【代充人】
     *
     * @param payAgentRechargeAccount 【代充人】
     * @return 结果
     */
    @Override
    public AjaxResult insertPayAgentRechargeAccount(PayAgentRechargeAccount payAgentRechargeAccount) {
        payAgentRechargeAccount.setCreateTime(DateUtils.getNowDate());
        if ( payAgentRechargeAccount.getRechargeDiscountRate() != null
                && payAgentRechargeAccount.getRechargeDiscountRate().compareTo( new BigDecimal( Constants.DISCOUNT_BILL_LIMIT ) ) >= 0 ) {
            return  AjaxResult.error( String.format( "充值优惠彩金比例不得大于%s，请重新设置！", Constants.DISCOUNT_BILL_LIMIT ) );

        }
        MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById(payAgentRechargeAccount.getAccount());
        if (memberInfo==null){
            return  AjaxResult.error("该会员ID不存在");
        }
        int i = payAgentRechargeAccountMapper.memberIdSearchRepeat(payAgentRechargeAccount.getAccount());
        if (i!=0){
            return  AjaxResult.error("该代充人账号已存在");
        }
        payAgentRechargeAccount.setNickName(memberInfo.getNickName());
        payAgentRechargeAccount.setBalanceAmount( BigDecimal.ZERO );
        payAgentRechargeAccount.setRechargeNum( 0 );
        payAgentRechargeAccountMapper.insertPayAgentRechargeAccount(payAgentRechargeAccount);
        return  AjaxResult.success();
    }

    /**
     * 修改【代充人】
     *
     * @param payAgentRechargeAccount 【代充人】
     * @return 结果
     */
    @Override
    public int updatePayAgentRechargeAccount(PayAgentRechargeAccount payAgentRechargeAccount) {
        return payAgentRechargeAccountMapper.updatePayAgentRechargeAccount(payAgentRechargeAccount);
    }

    /**
     * 批量删除【代充人】
     *
     * @param ids 需要删除的【代充人】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeAccountByIds(Long[] ids) {
        return payAgentRechargeAccountMapper.deletePayAgentRechargeAccountByIds(ids);
    }

    /**
     * 删除【代充人】信息
     *
     * @param id 【代充人】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentRechargeAccountById(Long id) {
        return payAgentRechargeAccountMapper.deletePayAgentRechargeAccountById(id);
    }
}
