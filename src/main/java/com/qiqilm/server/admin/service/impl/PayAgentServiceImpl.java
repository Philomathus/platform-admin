package com.qiqilm.server.admin.service.impl;

import com.google.common.collect.ImmutableMap;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.payagent.BasePayAgent;
import com.qiqilm.server.admin.payagent.PayAgentProcessorFactoryUtil;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IPayAgentService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
@Log4j2
public class PayAgentServiceImpl implements IPayAgentService {
    @Resource
    private PayAgentPlatformMapper  payAgentPlatformMapper;
    @Resource
    private MemberWithdrawLogMapper withdrawLogMapper;
    @Resource
    private PayAgentLogMapper       payAgentLogMapper;
    @Resource
    private SysUserMapper           sysUserMapper;
    @Resource
    private TokenService            tokenService;

    @Resource
    private PayAgentProcessorFactoryUtil payAgentProcessorFactoryUtil;
    @Resource
    private RedisUtil                    redisUtil;
    @Resource
    private SysConfigCacheUtil           sysConfigCacheUtil;
    @Resource
    private ILogService                  logService;
    @Resource
    private MemberInfoMapper             memberInfoMapper;
    @Resource
    private MemberBcodeMapper            memberBcodeMapper;

    @Value ( "${payAgentLimit:5000}" )
    private Integer payAgentLimit;
    @Value ( "${payAgentLimitShunWei:5000}" )
    private Integer payAgentLimitShunWei;
    @Value ( "${payAgentLimitBinLi:5000}" )
    private Integer payAgentLimitBinLi;
    @Value ( "${payAgentLimitTels:5000}" )
    private Integer payAgentLimitTels;
    @Value ( "${payAgentLimitLianFuBao:5000}" )
    private Integer payAgentLimitLianFuBao;
    @Value ( "${payAgentLimitMaYun:5000}" )
    private Integer payAgentLimitMaYun;
    @Value ( "${payAgentLimitHeZhong:5000}" )
    private Integer payAgentLimitHeZhong;
    @Value ( "${payAgentLimitDiDi:5000}" )
    private Integer payAgentLimitDiDi;
    @Value ( "${payAgentLimitNewMaxPay:5000}" )
    private Integer payAgentLimitNewMaxPay;
    @Value ( "${payAgentLimitYangGuangPay:5000}" )
    private Integer payAgentLimitYangGuangPay;
    @Value ( "${payAgentLimitDaFengChePay:5000}" )
    private Integer payAgentLimitDaFengChePay;
    @Value ( "${payAgentLimitLuBanPay:5000}" )
    private Integer payAgentLimitLuBanPay;
    @Value ( "${payAgentLimitDingFengPay:5000}" )
    private Integer payAgentLimitDingFengPay;
    @Value ( "${payAgentLimitHongBoPay:5000}" )
    private Integer payAgentLimitHongBoPay;
    @Value ( "${payAgentLimitXiaoYuePay:5000}" )
    private Integer payAgentLimitXiaoYuePay;
    @Value ( "${payAgentLimitYinLianPay:5000}" )
    private Integer payAgentLimitYinLianPay;
    @Value ( "${payAgentLimitAiNongPay:5000}" )
    private Integer payAgentLimitAiNongPay;
    @Value ( "${payAgentLimitZhongJiaPay:5000}" )
    private Integer payAgentLimitZhongJiaPay;
    @Value ( "${payAgentLimitLaoXiangPay:5000}" )
    private Integer payAgentLimitLaoXiangPay;
    @Value ( "${payAgentLimitFuLiangPay:5000}" )
    private Integer payAgentLimitFuLiangPay;
    @Value ( "${payAgentLimitHuiYuanPay:5000}" )
    private Integer payAgentLimitHuiYuanPay;
    @Value ( "${payAgentLimitShanDePay:5000}" )
    private Integer payAgentLimitShanDePay;
    @Value ( "${payAgentLimitGoPayPay:5000}" )
    private Integer payAgentLimitGoPayPay;
    @Value ( "${payAgentLimitOkPayPay:5000}" )
    private Integer payAgentLimitOkPayPay;
    @Value ( "${payAgentMiaoDaoFuPay:5000}" )
    private Integer payAgentMiaoDaoFuPay;
    @Value ( "${payAgentXinHuiPay:5000}" )
    private Integer payAgentXinHuiPay;
    @Value ( "${payAgentDaNiuPay:5000}" )
    private Integer payAgentDaNiuPay;
    @Value ( "${payAgentBaiDaFuPay:5000}" )
    private Integer payAgentBaiDaFuPay;
    @Value ( "${payAgentKuBiPay:5000}" )
    private Integer payAgentKuBiPay;
    @Value ( "${payAgentVipPay:5000}" )
    private Integer payAgentVipPay;
    @Value ( "${payAgentYunBeiPay:5000}" )
    private Integer payAgentYunBeiPay;
    @Value ( "${payAgentZhiYuanPay:5000}" )
    private Integer payAgentZhiYuanPay;
    @Value ( "${payAgentHYPay:5000}" )
    private Integer payAgentHYPay;
    @Value ( "${payAgentGTPay:5000}" )
    private Integer payAgentGTPay;
    @Value ( "${payAgentMiFengPay:5000}" )
    private Integer payAgentMiFengPay;
    @Value ( "${payAgentBBJiuDingPay:5000}" )
    private Integer payAgentBBJiuDingPay;
    @Value ( "${payAgentXinBeiPay:5000}" )
    private Integer payAgentXinBeiPay;
    @Value ( "${payAgentMyPay:5000}" )
    private Integer payAgentMyPay;
    @Value ( "${payAgentTianQiPay:5000}" )
    private Integer payAgentTianQiPay;
    @Value ( "${payAgent8kPay:5000}" )
    private Integer payAgent8kPay;
    @Value ( "${payAgentBoBiPay:5000}" )
    private Integer payAgentBoBiPay;
    @Value ( "${payAgentLeTuPay:5000}" )
    private Integer payAgentLeTuPay;

    @Override
    @Transactional ( rollbackFor = Exception.class )
    public void processOrderPay( MemberWithdrawLog withdrawLog, PayAgentLog payAgentLog, String orderNo,
                                 PayAgentPlatform payAgentPlatform, boolean isSuccess ) {
        Date              now            = new Date();
        MemberWithdrawLog newWithdrawLog = new MemberWithdrawLog();
        newWithdrawLog.setId( withdrawLog.getId() );
        newWithdrawLog.setStatus( isSuccess ? 6 : 5 );
        newWithdrawLog.setUpdateTime( now );
        newWithdrawLog.setRemark( "【" + payAgentPlatform.getName() + "】代付" + ( isSuccess ? "成功" : "失败" ) );
        withdrawLogMapper.updateMemberWithdrawLog( newWithdrawLog );

        //gopay提现彩金
        gopayWithdraw( withdrawLog, isSuccess );

        PayAgentLog newPayAgentLog = new PayAgentLog();
        newPayAgentLog.setPayAgentOrderNo( orderNo );
        newPayAgentLog.setCallbackTime( now );
        newPayAgentLog.setCallbackStatus( isSuccess ? 1 : 2 );
        if ( payAgentLog == null ) {
            newPayAgentLog.setCreateTime( now );
            newPayAgentLog.setWithdrawOrderNo( withdrawLog.getOrderNo() );
            newPayAgentLog.setWithdrawMoney( withdrawLog.getWithdrawMoney() );
            newPayAgentLog.setMemberAccount( withdrawLog.getAccount() );
            newPayAgentLog.setMemberId( withdrawLog.getMemberId() );
            newPayAgentLog.setPayAgentPlatId( payAgentPlatform.getId() );
            newPayAgentLog.setPayAgentPlatName( payAgentPlatform.getName() );
            payAgentLogMapper.insertPayAgentLog( newPayAgentLog );
        } else {
            newPayAgentLog.setId( payAgentLog.getId() );
            payAgentLogMapper.updatePayAgentLog( newPayAgentLog );
        }
    }

    @Override
    @Transactional ( rollbackFor = Exception.class )
    public void gopayWithdraw( MemberWithdrawLog withdrawLog, boolean isSuccess ) {
        //gopay的提现彩金
        if ( StringUtils.hasText( withdrawLog.getBankName() ) && withdrawLog.getBankName().contains( "GOPAY" ) ) {
            if ( isSuccess ) {
                String     gopayWithdraw = sysConfigCacheUtil.getConf( "gopayWithdraw", "" );
                BigDecimal chargeGive    = null;
                if ( StringUtils.hasText( gopayWithdraw ) ) {
                    chargeGive = new BigDecimal( gopayWithdraw ).multiply( withdrawLog.getWithdrawMoney() )
                            .setScale( 2, BigDecimal.ROUND_HALF_UP );
                    MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( withdrawLog.getMemberId() );
                    if ( chargeGive.compareTo( BigDecimal.ZERO ) > 0 ) {
                        logService.logMoneyAll( withdrawLog.getMemberId(), memberInfo.getUserName(), EnumMoney.chargegive,
                                memberInfo
                                        .getTotalAccount().add( chargeGive ), chargeGive, null, "gopay提现彩金",
                                withdrawLog.getOrderNo() + "_" + EnumMoney.chargegive.name() );
                    }
                    MemberBcode codeFlow = new MemberBcode();
                    codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
                    codeFlow.setIncome( chargeGive );
                    codeFlow.setCreateTime( new Date() );
                    codeFlow.setStatus( 0 );
                    codeFlow.setCur( BigDecimal.ZERO );
                    codeFlow.setUserId( memberInfo.getId() );
                    codeFlow.setDes( "gopay提现彩金" );
                    memberBcodeMapper.insertMemberBcode( codeFlow );
                    memberInfoMapper.updateMoneySelect( memberInfo.getId(), chargeGive, null, chargeGive, null, null );
                }
            }
        }
    }

    @Override
    public void queryAgent4Status5Min() {
        List<PayAgentLog> payAgentLogs = payAgentLogMapper.findNoCallback();

        List<PayAgentPlatform> payAgentPlatforms = payAgentPlatformMapper.selectPayAgentPlatformList( null );

        log.warn( "pal:" + payAgentLogs.size() + "pap:" + payAgentPlatforms.size() );

        for ( PayAgentLog payAgentLog : payAgentLogs ) {
            for ( PayAgentPlatform payAgentPlatform : payAgentPlatforms ) {
                if ( !Arrays.asList( ConstantsPayAgent.OK, ConstantsPayAgent.GOPAY, ConstantsPayAgent.HUOFENGHUANG )
                        .contains( payAgentPlatform.getCode() ) ) {
                    continue;
                }
                if ( payAgentLog.getPayAgentPlatId().toString().equals( payAgentPlatform.getId().toString() ) ) {
                    BasePayAgent basePayAgent = payAgentProcessorFactoryUtil.createPayProcessor( payAgentPlatform.getCode() );
                    try {
                        String a = basePayAgent.queryOrderPay( payAgentLog );
                        log.warn( "开始批量查询代付订单 - 订单号：{}，PayAgentPlatId：{},PayAgentPlatCode:{},查询结果:{}",
                                payAgentLog.getWithdrawOrderNo(), payAgentLog.getPayAgentPlatId(), payAgentPlatform.getCode(),
                                a );
                    } catch ( Exception e ) {
                        log.error( e.getMessage(), e );
                    }
                }
            }
        }
    }

    @Override
    public AjaxResult payAgentOrder( ReqPayAgent reqPayAgent ) throws Exception {
        if ( !StringUtils.hasText( reqPayAgent.getWithdrawOrderNo() ) || reqPayAgent.getPayAgentPlatId() == null ) {
            return AjaxResult.error( "订单号或代付平台ID不能为空" );
        }
        if ( reqPayAgent.getGoogleAuthCode() == null ) {
            return AjaxResult.error( "请输入google验证码" );
        }
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( reqPayAgent.getWithdrawOrderNo() );

        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( reqPayAgent.getPayAgentPlatId() );
        PayAgentLog      payAgentLog      = payAgentLogMapper.selectPayAgentLogOrderNo( reqPayAgent.getWithdrawOrderNo() );
        if ( payAgentLog != null ) {
            return AjaxResult.error( "该订单已有代付记录" );
        }
        if ( withdrawLog == null || payAgentPlatform == null ) {
            log.warn( "提现记录或代付平台未找到 - withdrawOrderNo:{};payAgentPlatId:{}", reqPayAgent.getWithdrawOrderNo(),
                    reqPayAgent.getPayAgentPlatId() );
            return AjaxResult.error( "提现记录或代付平台未找到" );
        }
        if ( withdrawLog.getWithdrawMoney() == null || withdrawLog.getWithdrawMoney().compareTo( BigDecimal.ZERO ) <= 0 ) {
            log.warn( "提现金额有误 - withdrawOrderNo:{};withdrawMoney:{}", reqPayAgent.getWithdrawOrderNo(),
                    withdrawLog.getWithdrawMoney() );
            return AjaxResult.error( "提现金额不得低于0元" );
        }
        if ( withdrawLog.getStatus() != 1 ) {
            return AjaxResult.error( "审核流程非法" );
        }

        /*if ( ( payAgentPlatform.getCode().contains( ConstantsPayAgent.LIAN_FU_BAO ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.LIAN_FU_BAO2 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO3 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.LIAN_FU_BAO4 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO5 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.LIAN_FU_BAO6 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO7 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.LIAN_FU_BAO8 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO9 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.LIAN_FU_BAO10 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO11 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.LIAN_FU_BAO12 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO13 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.LIAN_FU_BAO14 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO15 ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitLianFuBao ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitLianFuBao + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.TE_LUN_SU ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.TE_LUN_SU2 ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitTels ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitTels + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.DAFENGCHE ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.DAFENGCHE2 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.DAFENGCHE3 ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitDaFengChePay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitDaFengChePay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.BINLI ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.BINLI2 ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitBinLi ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitBinLi + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().contains( ConstantsPayAgent.SHUN_WEI ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.SHUN_WEI2 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI3 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.SHUN_WEI4 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI5 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.SHUN_WEI6 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI7 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.SHUN_WEI8 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI9 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.SHUN_WEI10 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI11 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.SHUN_WEI12 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI13 ) || payAgentPlatform.getCode()
                .equals( ConstantsPayAgent.SHUN_WEI14 )
                || payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI15 ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitShunWei ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitShunWei + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.Ma_Yun )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitMaYun ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitMaYun + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.HEZHONG )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitHeZhong ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitHeZhong + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.DIDI )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitDiDi ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitDiDi + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.NEWMAX )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitNewMaxPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitNewMaxPay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.YANGGUANG )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitYangGuangPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitYangGuangPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.LUBAN ) || payAgentPlatform.getCode().equals( ConstantsPayAgent.LUBAN2 ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitLuBanPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitLuBanPay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.HONGBO )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitHongBoPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitHongBoPay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.DINGFENG )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitDingFengPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitDingFengPay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.XIAOYUE )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitXiaoYuePay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitXiaoYuePay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.YINLIAN )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitYinLianPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitYinLianPay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().contains( ConstantsPayAgent.AINONG )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitAiNongPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitAiNongPay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.ZHONGJIA )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitZhongJiaPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitZhongJiaPay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.LAOXIANG )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitLaoXiangPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitLaoXiangPay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.SHANDE2 )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitShanDePay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitShanDePay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.MIAODAOFU )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentMiaoDaoFuPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentMiaoDaoFuPay + "元以上出款" );
        } else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.XINHUI )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentXinHuiPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentXinHuiPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.FULIANG ) || payAgentPlatform.getCode().equals( ConstantsPayAgent.FULIANG2 ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitFuLiangPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitFuLiangPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.HUIYUAN ) || payAgentPlatform.getCode().equals( ConstantsPayAgent.HUIYUAN2 ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitHuiYuanPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitHuiYuanPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.GOPAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitGoPayPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitGoPayPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.OK ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitOkPayPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLimitOkPayPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.DANIU ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentDaNiuPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentDaNiuPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.BAIDAFU ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentBaiDaFuPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentBaiDaFuPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.KUBI_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentKuBiPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentKuBiPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.VIP_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentVipPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentVipPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.YUNBEI_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentYunBeiPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentYunBeiPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.ZHIYUAN_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentZhiYuanPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentZhiYuanPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.HY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentHYPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentHYPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.GT_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentGTPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentGTPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.MIFENG_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentMiFengPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentMiFengPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.BB_JIUDING_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentBBJiuDingPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentBBJiuDingPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.XIN_BEI_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentXinBeiPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentXinBeiPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.MY_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentMyPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentMyPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.TIAN_QI_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentTianQiPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentTianQiPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent._8K_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgent8kPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgent8kPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.BO_BI_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentBoBiPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentBoBiPay + "元以上出款" );
        } else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.LE_TU_PAY ) )
                && withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLeTuPay ) ) > 0 ) {
            return AjaxResult.error( "此代付暂不支持" + payAgentLeTuPay + "元以上出款" );
        } else if ( withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimit ) ) > 0
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.Ma_Yun )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.VIP_PAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.ZHIYUAN_PAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.GT_PAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.MIFENG_PAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.HEZHONG )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.DIDI )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.MY_PAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent._8K_PAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LE_TU_PAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.BO_BI_PAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.TIAN_QI_PAY )
                && !payAgentPlatform.getCode().contains( ConstantsPayAgent.BINLI )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.BINLI2 )
                && !payAgentPlatform.getCode().contains( ConstantsPayAgent.BB_JIUDING_PAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.XIN_BEI_PAY )
                && !payAgentPlatform.getCode().contains( ConstantsPayAgent.LIAN_FU_BAO )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO2 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO3 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO4 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO5 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO6 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO7 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO8 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO9 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO10 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO11 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO12 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO13 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO14 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO15 )
                && !payAgentPlatform.getCode().contains( ConstantsPayAgent.SHUN_WEI )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI2 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI3 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI4 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI5 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI6 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI7 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI8 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI9 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI10 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI11 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI12 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI13 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI14 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI15 )
                && !payAgentPlatform.getCode().contains( ConstantsPayAgent.TE_LUN_SU )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.TE_LUN_SU2 )
                && !payAgentPlatform.getCode().contains( ConstantsPayAgent.DAFENGCHE )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.DAFENGCHE2 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.DAFENGCHE3 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.NEWMAX )
                && !payAgentPlatform.getCode().contains( ConstantsPayAgent.LUBAN )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LUBAN2 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.HONGBO )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.DINGFENG )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.XIAOYUE )
                && !payAgentPlatform.getCode().contains( ConstantsPayAgent.AINONG )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.ZHONGJIA )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.LAOXIANG )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHANDE2 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.YINLIAN )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.FULIANG )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.FULIANG2 )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.YANGGUANG )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.GOPAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.OK )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.MIAODAOFU )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.XINHUI )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.DANIU )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.BAIDAFU )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.KUBI_PAY )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.HUIYUAN )
                && !payAgentPlatform.getCode().equals( ConstantsPayAgent.HUIYUAN2 ) ) {
            return AjaxResult.error( "代付暂不支持" + payAgentLimit + "元以上出款" );
        }*/

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        if ( StringUtils.hasText( withdrawLog.getOpName() ) && !userName.equals( withdrawLog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + withdrawLog.getOpName() + "处理" );
        }

        String googleAuthSecret = sysUserMapper.selectGoogleAuthKeyByUserName( userName );

        if ( !StringUtils.hasText( googleAuthSecret ) ) {
            return AjaxResult.error( "未绑定google验证秘钥，无法审核" );
        }
        String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr(
                "secretkey" + "/googleAuthPrivateKey" ) );

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, reqPayAgent.getGoogleAuthCode() ) ) {
            return AjaxResult.error( "google验证码不正确，请检查" );
        }

        if ( !redisUtil.lock( EnumLock.payAgent, reqPayAgent.getWithdrawOrderNo(), "1", 10 ) ) {
            return AjaxResult.error( "请勿重复提交代付订单:" + reqPayAgent.getWithdrawOrderNo() );
        }

        int noFailCount = payAgentLogMapper.countNoFail( withdrawLog.getOrderNo() );
        if ( noFailCount > 0 ) {
            return AjaxResult.error( "此订单已被代付，请在三方后台跟踪订单状态" );
        }
        int platOrderCount = payAgentLogMapper.countPlatOrderNo( withdrawLog.getOrderNo(), reqPayAgent.getPayAgentPlatId() );
        if ( platOrderCount > 0 ) {
            return AjaxResult.error( String.format( "此订单已被 %s 处理过，请更换代付商后重试", payAgentPlatform.getName() ) );
        }

        reqPayAgent.setCurrentTime( new Date() );
        this.processOrder( payAgentPlatform, withdrawLog, reqPayAgent.getCurrentTime(), 4, 0 );

        BasePayAgent basePayAgent = payAgentProcessorFactoryUtil.createPayProcessor( payAgentPlatform.getCode() );
        if ( basePayAgent.orderPay( withdrawLog, payAgentPlatform, reqPayAgent ) ) {
            return AjaxResult.success( "代付订单提交成功" );
        }

        redisUtil.unLock( EnumLock.payAgent, reqPayAgent.getWithdrawOrderNo() );
        return AjaxResult.error( StringUtils.hasText( reqPayAgent.getFailReason() ) ?
                reqPayAgent.getFailReason() + ",请咨询三方" : "代付失败" );
    }

    @Override
    public AjaxResult payAgentOrders( ReqPayAgent reqPayAgent ) {
        if ( CollectionUtils.isEmpty( reqPayAgent.getWithdrawOrderNos() ) || reqPayAgent.getPayAgentPlatId() == null ) {
            return AjaxResult.error( "订单号或代付平台ID不能为空" );
        }
        if ( reqPayAgent.getGoogleAuthCode() == null ) {
            return AjaxResult.error( "请输入google验证码" );
        }
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( reqPayAgent.getPayAgentPlatId() );
        if ( payAgentPlatform == null ) {
            log.warn( "代付平台未找到 - payAgentPlatId:{}", reqPayAgent.getPayAgentPlatId() );
            return AjaxResult.error( "代付平台未找到" );
        }
        List<PayAgentLog> payAgentLogList = payAgentLogMapper.selectByAgentLogOrderList( reqPayAgent.getWithdrawOrderNos() );
        if ( payAgentLogList.size() > 0 ) {
            return AjaxResult.error( "被选中的订单已有代付记录" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        if ( !redisUtil.lock( EnumLock.payAgent, userName, "1", 10 ) ) {
            return AjaxResult.error( "代付订单提交过快" );
        }

        List<MemberWithdrawLog> withdrawLogs = withdrawLogMapper.selectPayAgentOrder( reqPayAgent.getWithdrawOrderNos(),
                userName );
        if ( CollectionUtils.isEmpty( withdrawLogs ) ) {
            return AjaxResult.error( "未匹配到可提现订单" );
        }

        String googleAuthSecret = sysUserMapper.selectGoogleAuthKeyByUserName( userName );

        if ( !StringUtils.hasText( googleAuthSecret ) ) {
            return AjaxResult.error( "未绑定google验证秘钥，无法审核" );
        }
        String googleAuthKey = "";
        try {
            googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr(
                    "secretkey" + "/googleAuthPrivateKey" ) );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, reqPayAgent.getGoogleAuthCode() ) ) {
            return AjaxResult.error( "google验证码不正确，请检查" );
        }

        BasePayAgent        basePayAgent   = payAgentProcessorFactoryUtil.createPayProcessor( payAgentPlatform.getCode() );
        Map<String, String> failReasonList = new TreeMap<>();
        int                 sucessNum      = 0;
        for ( MemberWithdrawLog withdrawLog : withdrawLogs ) {
            int noFailCount = payAgentLogMapper.countNoFail( withdrawLog.getOrderNo() );
            if ( noFailCount > 0 ) {
                failReasonList.put( withdrawLog.getOrderNo(), "此订单已被代付，请在三方后台跟踪订单状态" );
                continue;
            }
            int platOrderCount = payAgentLogMapper.countPlatOrderNo( withdrawLog.getOrderNo(), reqPayAgent.getPayAgentPlatId() );
            if ( platOrderCount > 0 ) {
                failReasonList.put( withdrawLog.getOrderNo(), String.format( "此订单已被 %s 处理过，请更换代付商后重试",
                        payAgentPlatform.getName() ) );
                continue;
            }

            ReqPayAgent newReqPayAgent = new ReqPayAgent();
            newReqPayAgent.setCurrentTime( new Date() );
            newReqPayAgent.setWithdrawOrderNo( withdrawLog.getOrderNo() );
            try {
                this.processOrder( payAgentPlatform, withdrawLog, newReqPayAgent.getCurrentTime(), 4, 0 );

                if ( basePayAgent.orderPay( withdrawLog, payAgentPlatform, newReqPayAgent ) ) {
                    sucessNum++;
                } else {
                    failReasonList.put( withdrawLog.getOrderNo(), newReqPayAgent.getFailReason() );
                }
            } catch ( Exception e ) {
                log.error( "代付下单失败 - 订单号：{};失败原因：{}", withdrawLog.getOrderNo(), e.getMessage(), e );
                failReasonList.put( withdrawLog.getOrderNo(), newReqPayAgent.getFailReason() );
            }
        }
        redisUtil.unLock( EnumLock.payAgent, userName );
        return AjaxResult.success( ImmutableMap.of( "fail", failReasonList, "sucess", sucessNum ) );
    }

    @Override
    @Transactional ( rollbackFor = Exception.class )
    public void processOrder( PayAgentPlatform payAgentPlatform, MemberWithdrawLog memberWithdrawLog, Date now, int status,
                              int orderState ) {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectMemberWithdrawLogById( memberWithdrawLog.getId() );
        PayAgentLog       payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( memberWithdrawLog.getOrderNo() );
        if ( !( withdrawLog.getStatus() == 1 || withdrawLog.getStatus() == 4 ) ) {
            throw new BusinessException( "审核流程非法" );
        }

        //gopay提现彩金
        gopayWithdraw( withdrawLog, status == 6 );

        // 更改withdrawLog状态
        MemberWithdrawLog newWithdrawLog = new MemberWithdrawLog();
        newWithdrawLog.setId( withdrawLog.getId() );
        newWithdrawLog.setBankCode( memberWithdrawLog.getBankCode() );
        newWithdrawLog.setStatus( status );
        newWithdrawLog.setType( 2 );
        newWithdrawLog.setUpdateTime( now );
        String remark;
        switch ( status ) {
            case 4:
                remark = "已交由【" + payAgentPlatform.getName() + "】出款";
                break;
            case 5:
                remark = "【" + payAgentPlatform.getName() + "】代付失败";
                break;
            case 6:
                remark = "【" + payAgentPlatform.getName() + "】代付成功";
                break;
            default:
                remark = "";
                break;
        }
        newWithdrawLog.setRemark( remark );
        log.warn( JsonUtil.object2Json( newWithdrawLog ) );
        withdrawLogMapper.updateMemberWithdrawLog( newWithdrawLog );


        // 保存代付信息日志
        PayAgentLog newPayAgentLog = new PayAgentLog();
        if ( StringUtils.hasText( memberWithdrawLog.getPayAgentOrderNo() ) ) {
            newPayAgentLog.setPayAgentOrderNo( memberWithdrawLog.getPayAgentOrderNo() );
        }
        switch ( status ) {
            case 4:
                newPayAgentLog.setCallbackStatus( 0 );
                break;
            case 5:
                newPayAgentLog.setCallbackTime( now );
                newPayAgentLog.setCallbackStatus( 2 );
                break;
            case 6:
                newPayAgentLog.setCallbackTime( now );
                newPayAgentLog.setCallbackStatus( 1 );
                break;
            default:
                break;
        }
        if ( payAgentLog == null ) {
            newPayAgentLog.setCreateTime( now );
            newPayAgentLog.setWithdrawOrderNo( withdrawLog.getOrderNo() );
            newPayAgentLog.setWithdrawMoney( withdrawLog.getWithdrawMoney() );
            newPayAgentLog.setMemberAccount( withdrawLog.getAccount() );
            newPayAgentLog.setMemberId( withdrawLog.getMemberId() );
            newPayAgentLog.setPayAgentPlatId( payAgentPlatform.getId() );
            newPayAgentLog.setPayAgentPlatName( payAgentPlatform.getName() );
            payAgentLogMapper.insertPayAgentLog( newPayAgentLog );
        } else {
            newPayAgentLog.setId( payAgentLog.getId() );
            payAgentLogMapper.updatePayAgentLog( newPayAgentLog );
        }
        log.warn( JsonUtil.object2Json( newPayAgentLog ) );
    }

    @Override
    @Transactional ( rollbackFor = Exception.class )
    public void callBackOrder( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform ) {
        // 更改withdrawLog状态
        MemberWithdrawLog newWithdrawLog = new MemberWithdrawLog();
        newWithdrawLog.setId( withdrawLog.getId() );
        newWithdrawLog.setStatus( 1 );
        newWithdrawLog.setUpdateTime( new Date() );
        newWithdrawLog.setRemark( String.format( "请求代付[%s]不成功", payAgentPlatform.getName() ) );
        int updateW = withdrawLogMapper.updateMemberWithdrawLog( newWithdrawLog );

        PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( withdrawLog.getOrderNo() );
        int         deleteP     = payAgentLogMapper.deletePayAgentLogById( payAgentLog.getId() );
        if ( updateW <= 0 || deleteP <= 0 ) {
            log.error( "代付状态回退失败:{}", withdrawLog.getOrderNo() );
            throw new BusinessException( "代付状态回退失败" );
        }
    }
}
