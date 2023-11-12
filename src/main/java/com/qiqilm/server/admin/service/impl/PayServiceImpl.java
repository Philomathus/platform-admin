package com.qiqilm.server.admin.service.impl;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.cache.MemberCacheManager;
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
    private   MemberRechargeLogMapper             rechargeLogMapper;
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
            MemberActionLogs log = new MemberActionLogs();
            log.setId( UuidUtil.getRandomUuidWithoutSeparator() );
            log.setUserId( payJour.getMemberId() );
            log.setUserName( payJour.getUserName() );
            log.setcTime( new Date() );
            log.setType( EnumAction.gm.getType() );
            log.setDes( EnumAction.gm.getDes() );
            log.setParam1( "加分资金：" + payJour.getSubMoney() );
            log.setParam2( "payJour订单号：" + orderNo );
            log.setParam3( "操作人：" + userName );
            log.setParam4( "备注：人工补单" );
            log.setParamIp( UserDataUtil.getIp( ServletUtil.getHttpServletRequest() ) );
            actionLogsMapper.insertMemberActionLogs( log );
            SpringUtils.getBean( IPayService.class ).updatePayJourStatus( payJour, "操作人：" + userName );
        }
        return AjaxResult.success();
    }

    @Transactional( rollbackFor = Exception.class )
    public boolean updatePayJourStatus( MemberPayJour payJour, String name ) {
        MemberPayJour memberPayJour = payJourMapper.selectMemberPayJourById( payJour.getId() );
        if ( "1".equals( memberPayJour.getStatus() ) ) {
            return false;
        }

        //更新支付订单状态
        MemberPayJour updatePayJour = new MemberPayJour();
        updatePayJour.setId( payJour.getId() );
        updatePayJour.setStatus( "1" );
        updatePayJour.setUpdateTime( DateFormatUtils.formate( new Date() ) );
        updatePayJour.setSubMoney( payJour.getSubMoney() );
        updatePayJour.setTradeSn( payJour.getTradeSn() );
        updatePayJour.setIsPatchOrder( payJour.getIsPatchOrder() );
        updatePayJour.setRemark( payJour.getRemark() );
        payJourMapper.updateMemberPayJour( updatePayJour );

        MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( payJour.getMemberId() );

        BigDecimal payJourMoney = payJour.getIsPatchOrder() == 1 ? payJour.getSubMoney() : memberPayJour.getMoney();

        BigDecimal payjourDiscountRate = new BigDecimal( sysConfigCacheUtil.getConf( "payjour_discount_rate" ) );

        // 充值彩金
        BigDecimal chargeGive = payjourDiscountRate.multiply( payJourMoney ).setScale( 2, RoundingMode.HALF_UP );

        //套利号无优惠
        if ( memberInfo.getStatus() == 4 ) {
            chargeGive = new BigDecimal( 0 );
        }

        BigDecimal firstRechargeCashBack = BigDecimal.ZERO; // 首冲赠送彩金
        if ( memberPayJour.getFirst() == 1 && sysConfigCacheUtil.getConfBool( "is_first_recharge_cash_back" ) ) {
            BigDecimal rebate = cashBackFirstRechargeMapper.selectByRechargeMoney( payJourMoney );
            if ( rebate != null && rebate.compareTo( BigDecimal.ZERO ) > 0 ) {
                firstRechargeCashBack = rebate;
            }
        }

        BigDecimal money = payJourMoney.add( chargeGive ).add( firstRechargeCashBack );

        BigDecimal nowmoney = memberInfo.getTotalAccount().add( payJourMoney );

        String orderId = payJour.getOrderNo();

        if ( chargeGive.compareTo( BigDecimal.ZERO ) > 0 ) {
            logService.logMoneyAll( memberInfo.getId(), memberInfo.getUserName(), EnumMoney.chargegive,
                    nowmoney.add( chargeGive ), chargeGive, null, name, orderId );
        }

        if ( firstRechargeCashBack.compareTo( BigDecimal.ZERO ) > 0 ) {
            logService.logMoneyAll( memberInfo.getId(), memberInfo.getUserName(), EnumMoney.wongive, nowmoney
                    .add( chargeGive )
                    .add( firstRechargeCashBack ), firstRechargeCashBack, null, "首冲赠送彩金；" + name, orderId );
        }

        logService.logMoneyAll( memberInfo.getId(), memberInfo.getUserName(), EnumMoney.charge, nowmoney, payJourMoney, null,
                name, orderId );

        BigDecimal codeMoney           = money;
        BigDecimal vipPayCodeMultiples = sysConfigCacheUtil.getConfBd( "vippay_income_code_multiples" );
        String     des                 = "线上充值";
        if ( "508".equals( memberPayJour.getPlatformId() ) && vipPayCodeMultiples.compareTo( BigDecimal.ZERO ) > 0
                && chargeGive.compareTo( BigDecimal.ZERO ) > 0 ) {
            codeMoney = money.multiply( vipPayCodeMultiples );
            des       = "vipPay充值" + vipPayCodeMultiples + "倍打码";
        }

        //新增佣金记录
        this.recommendProcess( payJour, memberInfo );
        MemberBcode codeFlow = new MemberBcode();
        codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
        codeFlow.setIncome( codeMoney );
        codeFlow.setCreateTime( new Date() );
        codeFlow.setStatus( 0 );
        codeFlow.setCur( BigDecimal.ZERO );
        codeFlow.setUserId( memberInfo.getId() );
        codeFlow.setDes( des );
        if ( memberBcodeMapper.insertMemberBcode( codeFlow ) > 0
                && memberInfoMapper.updateMoneySelect( memberInfo.getId(), money, null, codeMoney, null, null ) > 0 ) {
            MemberRechargeLog memberRechargeLog = new MemberRechargeLog();
            memberRechargeLog.setId( payJour.getId() );
            memberRechargeLog.setStatus( 3 );
            memberRechargeLog.setUpdateTime( new Date() );
            memberRechargeLog.setRechargeMoney( payJourMoney );
            rechargeLogMapper.updateMemberRechargeLog( memberRechargeLog );

            log.warn( "会员线上充值上分成功 - orderNo:{}", payJour.getOrderNo() );
            try {
                if ( memberInfo.getLevelIntegral().compareTo( BigDecimal.ZERO ) == 0
                        || memberInfo.getLevelIntegral().compareTo( memberInfo.getInviteMoney() ) <= 0 ) {
                    if ( memberInfo.getStatus() != 4 && memberInfo.getStatus() != 7 ) {
                        memberCacheManager.checkFirstChargeaddWheelTimes( memberInfo.getId() );
                    }
                }
            } catch ( Exception e ) {
                log.error( "首充报错", e );
            }
            this.paySendIm( memberInfo.getId(), payJourMoney );
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

    private void recommendProcess( MemberPayJour payJour, MemberInfo memberInfo ) {
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
