package com.qiqilm.server.admin.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.PayAgentRechargeRecord;
import com.qiqilm.server.admin.domain.PayAgentRechargeTradeLog;
import com.qiqilm.server.admin.domain.req.ReqPayAgentRechargeAccountLog;
import com.qiqilm.server.admin.mapper.PayAgentRechargeAccountMapper;
import com.qiqilm.server.admin.mapper.PayAgentRechargeTradeLogMapper;
import com.qiqilm.server.admin.service.IPayAgentRechargeRecordService;
import com.qiqilm.server.admin.service.IPayAgentRechargeTradeLogService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentRechargeAccountLogMapper;
import com.qiqilm.server.admin.domain.PayAgentRechargeAccountLog;
import com.qiqilm.server.admin.service.IPayAgentRechargeAccountLogService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    @Autowired
    private TokenService            tokenService;
    @Autowired
    private PayAgentRechargeTradeLogMapper payAgentRechargeTradeLogMapper;
    @Autowired
    private IPayAgentRechargeRecordService iPayAgentRechargeRecordService;


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

    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult refused(ReqPayAgentRechargeAccountLog req) {
        PayAgentRechargeAccountLog payAgentRechargeAccountLog = payAgentRechargeAccountLogMapper.selectPayAgentRechargeAccountLogById(req.getOrderNo());
        if ( payAgentRechargeAccountLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if (payAgentRechargeAccountLog.getStatus().equals("2") ) {
            return AjaxResult.error( "订单已经处理" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        payAgentRechargeAccountLog.setRemark( "拒绝原因:" + req.getRemark() );
        payAgentRechargeAccountLog.setStatus("2");
        payAgentRechargeAccountLog.setOpName( userName );
        payAgentRechargeAccountLog.setUpdateTime( DateUtils.getNowDate());
        payAgentRechargeAccountLogMapper.updatePayAgentRechargeAccountLog(payAgentRechargeAccountLog);
        PayAgentRechargeTradeLog payAgentRechargeTradeLog = new PayAgentRechargeTradeLog();
        payAgentRechargeTradeLog.setOrderNo( payAgentRechargeAccountLog.getOrderNo() + "_bank" );
        payAgentRechargeTradeLog.setAccount( payAgentRechargeAccountLog.getAccount() );
        payAgentRechargeTradeLog.setNickName( payAgentRechargeAccountLog.getNickName() );
        BigDecimal bigDecimal = new BigDecimal( 0 );
        payAgentRechargeTradeLog.setIncome( bigDecimal );
        payAgentRechargeTradeLog.setCreateTime( DateUtils.getNowDate() );
        payAgentRechargeTradeLog.setRemark( "拒绝人:" + userName + "拒绝金额："
                + payAgentRechargeAccountLog.getSubMoney() + "拒绝原因：" + req.getRemark() );
        payAgentRechargeTradeLog.setName( "申请拒绝" );
        payAgentRechargeTradeLogMapper.insertPayAgentRechargeTradeLog(payAgentRechargeTradeLog);
        return AjaxResult.success();
    }

    @Override
    public AjaxResult lock(ReqPayAgentRechargeAccountLog req) {
        PayAgentRechargeAccountLog payAgentRechargeAccountLog = payAgentRechargeAccountLogMapper.selectPayAgentRechargeAccountLogById(req.getOrderNo());
        if ( payAgentRechargeAccountLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        payAgentRechargeAccountLog.setRemark( "锁定人:" + userName );
        payAgentRechargeAccountLog.setStatus( "1" );//初审通过
        payAgentRechargeAccountLog.setOpName( userName );
        payAgentRechargeAccountLog.setUpdateTime( DateUtils.getNowDate() );
        payAgentRechargeAccountLogMapper.updatePayAgentRechargeAccountLog(payAgentRechargeAccountLog);
        return AjaxResult.success();
    }

    @Override
    public AjaxResult unlock(ReqPayAgentRechargeAccountLog req) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        PayAgentRechargeAccountLog payAgentRechargeAccountLog = payAgentRechargeAccountLogMapper.selectPayAgentRechargeAccountLogById(req.getOrderNo());
        if ( payAgentRechargeAccountLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( !payAgentRechargeAccountLog.getStatus().equals("1")) {
            return AjaxResult.error( "该订单已被处理,请刷新界面" );
        }
        if ( !StringUtils.isEmpty( payAgentRechargeAccountLog.getOpName() ) && !userName.equals( payAgentRechargeAccountLog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + payAgentRechargeAccountLog.getOpName() + "处理" );
        }
        payAgentRechargeAccountLog.setRemark( "取消锁定人：" + userName );
        payAgentRechargeAccountLog.setStatus( "0");
        payAgentRechargeAccountLog.setOpName( "" );
        payAgentRechargeAccountLog.setUpdateTime( DateUtils.getNowDate() );
        payAgentRechargeAccountLogMapper.updatePayAgentRechargeAccountLog(payAgentRechargeAccountLog);
        return AjaxResult.success();
    }

    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult artificial(ReqPayAgentRechargeAccountLog req) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        PayAgentRechargeAccountLog payAgentRechargeAccountLog = payAgentRechargeAccountLogMapper.selectPayAgentRechargeAccountLogById(req.getOrderNo());
        if ( payAgentRechargeAccountLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( "3".equals(payAgentRechargeAccountLog.getStatus()) ) {
            return AjaxResult.error( "订单已经处理" );
        }
        String     Account       = payAgentRechargeAccountLog.getAccount();
        BigDecimal BalanceAmount = payAgentRechargeAccountLog.getSubMoney();
        payAgentRechargeAccountLogMapper.updateByBalanceAmount( Account, BalanceAmount );
        PayAgentRechargeAccountLog update = new PayAgentRechargeAccountLog();
        update.setOrderNo( payAgentRechargeAccountLog.getOrderNo() );
        update.setStatus( "3" );
        update.setRemark( "存入人:" + userName );
        update.setOpName( userName );
        update.setUpdateTime( DateUtils.getNowDate() );
        payAgentRechargeAccountLogMapper.updatePayAgentRechargeAccountLog(update);

        //存提表新增记录
        PayAgentRechargeRecord payAgentRechargeRecord = new PayAgentRechargeRecord();
        payAgentRechargeRecord.setOrderNo( payAgentRechargeAccountLog.getOrderNo() + "_bank" );
        payAgentRechargeRecord.setRechargeNickName( payAgentRechargeAccountLog.getNickName() );
        payAgentRechargeRecord.setRechargeAcount( payAgentRechargeAccountLog.getAccount() );
        payAgentRechargeRecord.setType( "代充人入款存入" );
        payAgentRechargeRecord.setRemark( "存入人:" + userName );
        payAgentRechargeRecord.setMoney( payAgentRechargeAccountLog.getSubMoney() );
        payAgentRechargeRecord.setOpName( userName );
        payAgentRechargeRecord.setCreateTime( DateUtils.getNowDate() );
        iPayAgentRechargeRecordService.insertPayAgentRechargeRecord(payAgentRechargeRecord);

        //给pay_agent_recharge_trade_log增加记录
        PayAgentRechargeTradeLog payAgentRechargeTradeLog = new PayAgentRechargeTradeLog();
        payAgentRechargeTradeLog.setOrderNo( payAgentRechargeAccountLog.getOrderNo() + "_bank" );
        payAgentRechargeTradeLog.setAccount( payAgentRechargeAccountLog.getAccount() );
        payAgentRechargeTradeLog.setNickName( payAgentRechargeAccountLog.getNickName() );
        payAgentRechargeTradeLog.setIncome( payAgentRechargeAccountLog.getSubMoney() );
        payAgentRechargeTradeLog.setCreateTime( DateUtils.getNowDate() );
        payAgentRechargeTradeLog.setRemark( "存入人:" + userName );
        payAgentRechargeTradeLog.setName( "申请存入" );
        payAgentRechargeTradeLogMapper.insertPayAgentRechargeTradeLog(payAgentRechargeTradeLog);
        return AjaxResult.success();
    }

    @Override
    public AjaxResult statistic(PayAgentRechargeAccountLog rechargeAccountLog) {
        PayAgentRechargeAccountLog payAgentRechargeAccountLog = payAgentRechargeAccountLogMapper.sumMoney(rechargeAccountLog);
        return AjaxResult.success(payAgentRechargeAccountLog);
    }
}
