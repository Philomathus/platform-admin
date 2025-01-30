package com.qiqilm.server.admin.service.impl;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.cache.MemberCacheManager;
import com.qiqilm.server.admin.cache.PayCacheUtil;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.enums.EnumAction;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IPayService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
public class PayServiceImpl implements IPayService {
    @Autowired
    private   MemberPayJourMapper                 payJourMapper;
    @Autowired
    private   SysUserMapper                       sysUserMapper;
    @Autowired
    private   MemberInfoMapper                    memberInfoMapper;
    @Autowired
    private   MemberActionLogsMapper              actionLogsMapper;
    @Autowired
    private   MemberBcodeMapper                   memberBcodeMapper;
    @Autowired
    private   MemberRecommendMapper               recommendMapper;
    @Autowired
    private   ConfigRecommendMapper               configRecommendMapper;
    @Autowired
    private   ActivityCashBackFirstRechargeMapper cashBackFirstRechargeMapper;
    @Autowired
    private   TokenService                        tokenService;
    @Autowired
    private   ILogService                         logService;
    @Autowired
    private   MemberCacheManager                  memberCacheManager;
    @Autowired
    private   SysConfigCacheUtil                  sysConfigCacheUtil;
    @Autowired
    protected RestTemplate                        restTemplate;
    @Autowired
    private   PayCacheUtil                        payCacheUtil;

    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult payPatchOrder( Map<String, Object> requestMap ) throws Exception {
        String orderNo        = ( String ) requestMap.get( "orderNo" );
        String subMoney       = ( String ) requestMap.get( "subMoney" );
        String googleAuthCode = ( String ) requestMap.get( "googleAuthCode" );
        if ( !StringUtils.hasText( googleAuthCode ) ) {
            return AjaxResult.error( "请输入google验证码" );
        }
        MemberPayJour payJour = payJourMapper.findByOrderNo( orderNo );
        if ( "1".equals( payJour.getStatus() ) ) {
            return AjaxResult.error( "订单状态有误，补单失败" );
        }

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        String googleAuthSecret = sysUserMapper.selectGoogleAuthKeyByUserName( userName );

        if ( !StringUtils.hasText( googleAuthSecret ) ) {
            return AjaxResult.error( "未绑定google验证秘钥，无法审核" );
        }
        String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr(
                "secretkey" + "/googleAuthPrivateKey" ) );

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, Integer.parseInt( googleAuthCode ) ) ) {
            return AjaxResult.error( "google验证码不正确，请检查" );
        }

        payJour.setSubMoney( new BigDecimal( subMoney ) );
        payJour.setIsPatchOrder( 1 );
        payJour.setRemark( "操作人：" + userName + "人工补单" );

        if ( !"1".equals( payJour.getStatus() ) ) {
            MemberActionLogs actionLogs = new MemberActionLogs();
            actionLogs.setId( UuidUtil.getRandomUuidWithoutSeparator() );
            actionLogs.setUserId( payJour.getMemberId() );
            actionLogs.setUserName( payJour.getUserName() );
            actionLogs.setcTime( new Date() );
            actionLogs.setType( EnumAction.gm.getType() );
            actionLogs.setDes( EnumAction.gm.getDes() );
            actionLogs.setParam1( "加分资金：" + payJour.getSubMoney() );
            actionLogs.setParam2( "payJour订单号：" + orderNo );
            actionLogs.setParam3( "操作人：" + userName );
            actionLogs.setParam4( "备注：人工补单" );
            actionLogs.setParamIp( UserDataUtil.getIp( ServletUtil.getHttpServletRequest() ) );
            actionLogsMapper.insertMemberActionLogs( actionLogs );
            try {
                SpringUtils.getBean( IPayService.class ).updatePayJourStatus( payJour, "操作人：" + userName );
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
        }
        return AjaxResult.success();
    }

    @Transactional( rollbackFor = Exception.class )
    public boolean updatePayJourStatus( MemberPayJour payJour, String name ) {
        MemberPayJour memberPayJour = payJourMapper.selectMemberPayJourById( payJour.getId() );
        if ( "1".equals( memberPayJour.getStatus() ) ) {
            return false;
        }
        PayServiceImpl payService = SpringUtils.getAopProxy( this );

        Date now = new Date();
        //更新支付订单状态
        MemberPayJour updatePayJour = new MemberPayJour();
        updatePayJour.setId( payJour.getId() );
        updatePayJour.setStatus( "1" );
        updatePayJour.setUpdateTime( DateFormatUtils.formate( now ) );
        updatePayJour.setSubMoney( payJour.getSubMoney() );
        updatePayJour.setTradeSn( payJour.getTradeSn() );
        updatePayJour.setIsPatchOrder( payJour.getIsPatchOrder() );
        updatePayJour.setRemark( payJour.getRemark() );

        MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( payJour.getMemberId() );
        if ( memberInfo.getLevelIntegral().compareTo( BigDecimal.ZERO ) == 0
                || memberInfo.getLevelIntegral().compareTo( memberInfo.getInviteMoney() ) <= 0 ) {
            updatePayJour.setFirst( 1L );
        } else {
            updatePayJour.setFirst( 0L );
        }

        BigDecimal payJourMoney = payJour.getIsPatchOrder() == 1 ? payJour.getSubMoney() : memberPayJour.getMoney();

        //支付通道优惠比例
        BigDecimal    chargeGive    = null;
        PayChannelNew payChannelNew = null;
        if ( memberPayJour.getChannelId() != null ) {
            payChannelNew = payCacheUtil.getPayChannel( Long.valueOf( memberPayJour.getChannelId() ) );
        }

        String firstRechargeRate = sysConfigCacheUtil.getConf( "pay_first_recharge_rate" );
        String nextRechargeRate  = sysConfigCacheUtil.getConf( "pay_next_recharge_rate" );

        if ( StringUtils.hasText( firstRechargeRate ) && StringUtils.hasText( nextRechargeRate ) ) {
            String[] payFirstPlatformRates = firstRechargeRate.split( ";" );

            String[] payNextPlatformRates = nextRechargeRate.split( ";" );

            for ( String payPlatformRate : payFirstPlatformRates ) {
                String[] firstPaySplit = payPlatformRate.split( "," );
                if ( memberPayJour.getPlatformId().equals( firstPaySplit[ 0 ] )
                        && payJourMapper.successTodayCount( memberPayJour.getMemberId(), memberPayJour.getPlatformId() ) == 0 ) {
                    BigDecimal firstRate = new BigDecimal( firstPaySplit[ 1 ] );
                    chargeGive = payJourMoney.multiply( firstRate ).setScale( 2, RoundingMode.HALF_UP );
                    log.warn( "首冲 {},{},{}", chargeGive, memberPayJour.getPlatformId(), memberPayJour.getMemberId() );
                    break;
                }
            }
            if ( chargeGive == null ) {
                for ( String payPlatformRate : payNextPlatformRates ) {
                    String[] firstPaySplit = payPlatformRate.split( "," );
                    if ( memberPayJour.getPlatformId().equals( firstPaySplit[ 0 ] )
                            && payJourMapper.successTodayCount( memberPayJour.getMemberId(), memberPayJour.getPlatformId() )
                            > 0 ) {
                        BigDecimal firstRate = new BigDecimal( firstPaySplit[ 1 ] );
                        chargeGive = payJourMoney.multiply( firstRate ).setScale( 2, RoundingMode.HALF_UP );
                        log.warn( "每笔 {},{},{}", chargeGive, memberPayJour.getPlatformId(), memberPayJour.getMemberId() );
                        break;
                    }
                }
            }
        }
        if ( chargeGive == null ) {
            if ( "508".equals( memberPayJour.getPlatformId() ) ) { // 508 是vipPay

                String newVipPayRate = sysConfigCacheUtil.getConf( "new_vippay_rate" );

                if ( org.apache.commons.lang3.StringUtils.isNotBlank( newVipPayRate ) ) {
                    chargeGive = BigDecimal.ZERO;
                    String[] newVipPayRates = newVipPayRate.split( ";" );
                    for ( String rates : newVipPayRates ) {
                        String[]   spit   = rates.split( "," );
                        BigDecimal amount = new BigDecimal( spit[ 0 ] );
                        if ( payJourMoney.compareTo( amount ) >= 0 ) {
                            chargeGive = payJourMoney.multiply( new BigDecimal( spit[ 1 ] ) ).setScale( 2, RoundingMode.HALF_UP );
                        }

                    }
                } else {
                    chargeGive = sysConfigCacheUtil
                            .getConfBd( "vippay_rate" )
                            .multiply( payJourMoney )
                            .setScale( 2, RoundingMode.HALF_UP );
                }

            } else if ( payChannelNew != null && StringUtils.hasText( payChannelNew.getDiscountBill() ) ) {
                chargeGive = new BigDecimal( payChannelNew.getDiscountBill() )
                        .multiply( payJourMoney )
                        .setScale( 2, RoundingMode.HALF_UP );
            } else {
                chargeGive = BigDecimal.ZERO;
            }
        }

        //套利号无优惠
        if ( memberInfo.getStatus() == 4 ) {
            chargeGive = BigDecimal.ZERO;
        }

        //		BigDecimal payjourDiscountRate = sysConfigCacheUtil.getConfBd( "payjour_discount_rate" );
        //
        //		// 充值彩金
        //		BigDecimal chargeGive = payjourDiscountRate.multiply( payJourMoney ).setScale( 2, BigDecimal.ROUND_HALF_UP );

        BigDecimal firstRechargeCashBack = BigDecimal.ZERO; // 首冲赠送彩金
        if ( updatePayJour.getFirst() == 1 && sysConfigCacheUtil.getConfBool( "is_first_recharge_cash_back" ) ) {
            BigDecimal rebate = cashBackFirstRechargeMapper.selectByRechargeMoney( payJourMoney );
            if ( rebate != null && rebate.compareTo( BigDecimal.ZERO ) > 0 ) {
                firstRechargeCashBack = rebate;
            }
        }

        BigDecimal money = payJourMoney.add( chargeGive ).add( firstRechargeCashBack );

        BigDecimal nowmoney = memberInfo.getTotalAccount().add( payJourMoney );

        String orderId = payJour.getOrderNo();

        payJourMapper.updateMemberPayJour( updatePayJour );
        if ( chargeGive.compareTo( BigDecimal.ZERO ) > 0 ) {
            logService.logMoneyAll( memberInfo.getId(), memberInfo.getUserName(), EnumMoney.chargegive,
                    nowmoney.add( chargeGive ), chargeGive, null, name,
                    orderId + "_" + EnumMoney.chargegive.name() );
        }

        if ( firstRechargeCashBack.compareTo( BigDecimal.ZERO ) > 0 ) {
            logService.logMoneyAll( memberInfo.getId(), memberInfo.getUserName(), EnumMoney.wongive, nowmoney
                            .add( chargeGive )
                            .add( firstRechargeCashBack ), firstRechargeCashBack, null,
                    "首冲赠送彩金；" + name, orderId + "_" + EnumMoney.wongive.name() );
        }

        logService.logMoneyAll( memberInfo.getId(), memberInfo.getUserName(), EnumMoney.charge, nowmoney, payJourMoney, null,
                name, orderId );

        BigDecimal codeMoney           = money;
        String     des                 = "线上充值";
        if ( "508".equals( memberPayJour.getPlatformId() ) ) {
            BigDecimal vipPayCodeMultiples = sysConfigCacheUtil.getConfBd( "vippay_income_code_multiples" );
            if ( vipPayCodeMultiples.compareTo( BigDecimal.ZERO ) > 0 ) {
                codeMoney = money.multiply( vipPayCodeMultiples );
                des       = "vipPay充值" + vipPayCodeMultiples + "倍打码";
            }
        }

        if ( "702".equals( memberPayJour.getPlatformId() ) ) {
            BigDecimal abPayCodeMultiples = sysConfigCacheUtil.getConfBd( "abpay_income_code_multiples" );
            if ( abPayCodeMultiples.compareTo( BigDecimal.ZERO ) > 0 ) {
                codeMoney = money.multiply( abPayCodeMultiples );
                des       = "abPay充值" + abPayCodeMultiples + "倍打码";
            }
        }

        //新增佣金记录
        payService.recommendProcess( payJour, memberInfo );
        MemberBcode codeFlow = new MemberBcode();
        codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
        codeFlow.setIncome( codeMoney );
        codeFlow.setCreateTime( now );
        codeFlow.setStatus( 0 );
        codeFlow.setCur( BigDecimal.ZERO );
        codeFlow.setUserId( memberInfo.getId() );
        codeFlow.setDes( des );
        if ( memberBcodeMapper.insertMemberBcode( codeFlow ) > 0
                && memberInfoMapper.updateMoneySelect( memberInfo.getId(), money, null, codeMoney, null, null ) > 0 ) {
            log.warn( "会员线上充值上分成功 - orderNo:{}", payJour.getOrderNo() );
            try {
                if ( memberInfo.getLevelIntegral().compareTo( BigDecimal.ZERO ) == 0
                        || memberInfo.getLevelIntegral().compareTo( memberInfo.getInviteMoney() ) <= 0 ) {
                    if ( memberInfo.getStatus() != 4 && memberInfo.getStatus() != 7 ) {
                        memberCacheManager.checkFirstChargeaddWheelTimes( memberInfo.getId() );
                    }
                }
            } catch ( Exception e ) {
                log.error( "首充报错 {}", e.getMessage(), e );
            }
            payService.paySendIm( memberInfo.getId(), payJourMoney );
            return true;
        }
        return false;
    }

    @Async
    public void paySendIm( String userId, BigDecimal orderAmount ) {
        String pay_seccess_im_url = sysConfigCacheUtil.getConf( "pay_seccess_im_url" );
        if ( !StringUtils.hasText( pay_seccess_im_url ) ) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put( "userId", userId );
        params.put( "orderAmount", String.valueOf( orderAmount ) );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( params, httpHeaders );

        try {
            restTemplate.execute( pay_seccess_im_url, HttpMethod.POST, restTemplate.httpEntityCallback( httpEntity ),
                    response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                log.warn( text );
                return text;
            } );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
    }

    public void recommendProcess( MemberPayJour payJour, MemberInfo memberInfo ) {
        if ( StringUtils.hasText( memberInfo.getInviterCode() ) ) {
            Map<Integer, ConfigRecommend> billMap = configRecommendMapper
                    .selectConfigRecommendList( null )
                    .stream()
                    .collect( Collectors.toMap( ConfigRecommend::getLevel, Function.identity() ) );

            MemberInfo rd1 = memberInfoMapper.findRecommendByInviterCode( memberInfo.getInviterCode() );
            MemberInfo rd2 = null;

            //一级分佣
            if ( rd1 != null ) {
                BigDecimal      commission       = payJour.getMoney().multiply( billMap.get( 1 ).getBill() );
                MemberRecommend recommendUserLog = new MemberRecommend();
                recommendUserLog.setId( UuidUtil.getRandomUuidWithoutSeparator() );
                recommendUserLog.setCreateTime( new Date() );
                recommendUserLog.setMemberId( payJour.getMemberId() );
                recommendUserLog.setMemberName( payJour.getUserName() );
                recommendUserLog.setLevel( 1 );
                recommendUserLog.setInviterId( rd1.getId() );
                recommendUserLog.setInviter( rd1.getUserName() );
                recommendUserLog.setCommission( commission );
                recommendUserLog.setStatus( 0 );
                recommendUserLog.setCode( memberInfo.getMemberCode() );
                recommendUserLog.setOrderMoney( payJour.getMoney() );
                recommendMapper.insertMemberRecommend( recommendUserLog );
                if ( StringUtils.hasText( rd1.getInviterCode() ) ) {
                    rd2 = memberInfoMapper.findRecommendByInviterCode( memberInfo.getInviterCode() );
                }
            }
            //二级分佣
            if ( rd2 != null ) {
                BigDecimal      commission       = payJour.getMoney().multiply( billMap.get( 2 ).getBill() );
                MemberRecommend recommendUserLog = new MemberRecommend();
                recommendUserLog.setId( UuidUtil.getRandomUuidWithoutSeparator() );
                recommendUserLog.setCreateTime( new Date() );
                recommendUserLog.setMemberId( payJour.getMemberId() );
                recommendUserLog.setMemberName( payJour.getUserName() );
                recommendUserLog.setLevel( 2 );
                recommendUserLog.setCommission( commission );
                recommendUserLog.setInviterId( rd2.getId() );
                recommendUserLog.setInviter( rd2.getUserName() );
                recommendUserLog.setCode( memberInfo.getMemberCode() );
                recommendUserLog.setStatus( 0 );
                recommendUserLog.setOrderMoney( payJour.getMoney() );
                recommendMapper.insertMemberRecommend( recommendUserLog );
            }
        }
    }
}
