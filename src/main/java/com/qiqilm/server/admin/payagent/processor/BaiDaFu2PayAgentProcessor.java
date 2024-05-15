package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.LocalDateTimeUtils;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.BAIDAFU2 + "PayAgentProcessor" )
@Log4j2
public class BaiDaFu2PayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();

        bodyMap.put( "MerchantId", payAgentPlatform.getMerId() );
        bodyMap.put( "Amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "BankCardBankName", withdrawLog.getBankName() );
        bodyMap.put( "BankCardNumber", withdrawLog.getBankAccount() );
        bodyMap.put( "BankCardRealName", withdrawLog.getBankUserName() );
        bodyMap.put( "MerchantUniqueOrderId", withdrawLog.getOrderNo() );
        bodyMap.put( "NotifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put( "Remark", withdrawLog.getOrderNo() );
        bodyMap.put( "WithdrawTypeId", 0 );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr ).toLowerCase();

        bodyMap.put( "Sign", sign );
        bodyMap.put( "Timestamp", LocalDateTimeUtils.format( LocalDateTime.now(), LocalDateTimeUtils.YYYYMMDDHHMMSS_FORMATTER ) );

        log.warn( tempStr );
        log.warn( JsonUtil.object2Json( bodyMap ) );
        log.warn( "sign: {}", sign );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( bodyMap ),
                reqPayAgent );
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "0".equals( resultMap.getOrDefault( "Code", "" ).toString() ) ) {
                log.info( payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "Message", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "fail";
        }

        String sign    = requestMap.remove( "Sign" ).toString();
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        SortedMap<String, Object> dataMap = new TreeMap<>( requestMap );
        String                    tempStr = assemblyUrl( dataMap ) + signMd5;
        log.warn( tempStr );
        String signStr = DigestUtils.md5Hex( tempStr ).toLowerCase();
        log.info( payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr );

        if ( sign.equalsIgnoreCase( signStr ) ) {
            String order_num = requestMap.getOrDefault( "MerchantUniqueOrderId", "" ).toString();
            String status    = requestMap.getOrDefault( "WithdrawOrderStatus", "" ).toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( order_num );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", order_num );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 3 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", order_num );
                return "SUCCESS";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", order_num );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( order_num );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, requestMap.getOrDefault( "WithdrawOrderId", "" )
                                                                                 .toString(), payAgentPlatform,
                    "100".equals( status ) );
            log.info(
                    payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "100".equals( status ) ? "成功" : "失败" );
            return "SUCCESS";
        }

        return "fail";
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
        paramsMap.put( "MerchantId", payAgentPlatform.getMerId() );
        paramsMap.put( "MerchantUniqueOrderId", withdrawLog.getOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( paramsMap ) + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr ).toLowerCase();
        paramsMap.put( "Sign", sign );

        log.warn( JsonUtil.object2Json( paramsMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageForm( paramsMap ),
                null );
        log.info( payAgentPlatform.getName()
                + "查询结果{}，订单号：{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String trade_state = resultMap.getOrDefault( "WithdrawOrderStatus", "" ).toString();
            if ( "100".equals( trade_state ) || "0".equals( trade_state ) || "-90".equals( trade_state ) ) {
                // status 4代付中 5代付失败 6代付成功
                // trade_state  100成功 -90失败 0 處理中,需繼續查詢
                int status      = 4;
                int orderStatus = 0;
                if ( "100".equals( trade_state ) ) {
                    status      = 6;
                    orderStatus = 1;
                } else if ( "-90".equals( trade_state ) ) {
                    status      = 5;
                    orderStatus = 2;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderStatus );
            }
            return resultMap.getOrDefault("Message", "").toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
