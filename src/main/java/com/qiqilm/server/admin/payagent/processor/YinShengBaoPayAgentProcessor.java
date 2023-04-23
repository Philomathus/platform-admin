package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.YIN_SHENG_BAO_PAY + "PayAgentProcessor" )
@Log4j2
public class YinShengBaoPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "t_ChannelId", payAgentPlatform.getHeaderKey() );
        bodyMap.put( "t_HolderName", withdrawLog.getBankUserName() );
        bodyMap.put( "t_MerchantTradeNo", withdrawLog.getOrderNo() );
        bodyMap.put( "t_HolderPhone", "" );
        bodyMap.put( "t_HolderIdNum", "" );
        bodyMap.put( "t_BankName", withdrawLog.getBankName() );
        bodyMap.put( "t_SubbranchName", withdrawLog.getBankAddress() );
        bodyMap.put( "t_BankNo", "" );
        bodyMap.put( "t_Amount", withdrawLog.getWithdrawMoney().multiply( new BigDecimal( 100 ) )
                                            .setScale( 0, RoundingMode.HALF_UP ) );
        bodyMap.put( "t_AccountType", "对私" );
        bodyMap.put( "t_Remark", "提成结算" );
        bodyMap.put( "t_CardNo", withdrawLog.getBankAccount() );
        bodyMap.put( "t_NotifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        bodyMap.values().removeIf( v -> StringUtils.isBlank( v.toString() ) );
        String tempStr = this.assemblyUrl( bodyMap );
        bodyMap.put( "t_Sign", RSACoder.signSha1Rsa( tempStr, payAgentPlatform.getSignPrivateKey() ) );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String success = resultMap.getOrDefault( "code", "" ).toString();
            if ( "200".equals( success ) ) {
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "message", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "failed";
        }

        String rspSign = requestMap.remove( "sign" ).toString();

        requestMap.values().removeIf( v -> StringUtils.isBlank( v.toString() ) );
        SortedMap<String, Object> dataMap = new TreeMap<>( requestMap );

        String tempStr = this.assemblyUrl( dataMap );

        if ( RSACoder.verifySha1Rsa( tempStr, payAgentPlatform.getSignPublicKey(), rspSign ) ) {
            String merOrder = requestMap.getOrDefault( "n_MerTradeNo", "" ).toString();
            String tradeNo  = requestMap.getOrDefault( "n_TradeNo", "" ).toString();
            String status   = requestMap.getOrDefault( "n_Status", "" ).toString();
            if ( StringUtils.isBlank( merOrder ) ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", merOrder );
                return "failed";
            }
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( merOrder );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", merOrder );
                return "failed";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", merOrder );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( merOrder );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, tradeNo, payAgentPlatform, "01".equals( status ) );
            return "success";
        }

        return "failed";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                     String realIp ) throws Exception {
        return null;
    }

    @Override
    public String queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
        MemberWithdrawLog withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform  payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );

        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put( "t_MerchantId", payAgentPlatform.getMerId() );

        String tempStr = this.assemblyUrl( paramsMap );
        paramsMap.put( "t_Sign", RSACoder.signSha1Rsa( tempStr, payAgentPlatform.getSignPrivateKey() ) );

        log.warn( JsonUtil.object2Json( paramsMap ) );

        Map<String, Object> resultMap = this.sendPostMap(
                payAgentPlatform.getPayOrderQueryAddr() + withdrawLog.getOrderNo(), packageJson( paramsMap ), null );
        log.info( payAgentPlatform.getName()
                + "查询结果{}，订单号：{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) && "200".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {

            Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
            if ( !CollectionUtils.isEmpty( dataMap ) ) {
                String status       = dataMap.getOrDefault( "status", "" ).toString();
                int    orderState   = "01".equals( status ) ? 1 : 2;
                int    resultStatus = "01".equals( status ) ? 6 : 5;
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), resultStatus,
                        orderState );
            }
            return resultMap.getOrDefault( "message", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
