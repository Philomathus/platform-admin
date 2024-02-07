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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.XIANG_YANG_PAY + "PayAgentProcessor" )
@Log4j2
public class XiangYangPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "api_code", payAgentPlatform.getMerId() );
        bodyMap.put( "order_id", withdrawLog.getOrderNo() );
        bodyMap.put( "cash_money", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put( "time", System.currentTimeMillis() );
        bodyMap.put( "bank_compellation", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "bank_account_number", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "bank_branch", withdrawLog.getBankName() );
        bodyMap.put( "bank_code", "OTC" );
        bodyMap.put( "t", 0 );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr ).toUpperCase();
        bodyMap.put( "sign", sign );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "return_code", "" ).toString();
            if ( "SUCCESS".equals( code ) ) {
                log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "return_msg", "" ).toString() );

                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String                    sign      = requestMap.remove( "sign" ).toString();
        String                    status    = requestMap.getOrDefault( "code", "" ).toString();
        String                    paysapiId = requestMap.getOrDefault( "paysapi_id", "" ).toString();
        SortedMap<String, Object> bodyMap   = new TreeMap<>( requestMap );
        bodyMap.values().removeIf( value -> value == null || StringUtils.isBlank( value.toString() ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex( tempStr ).toUpperCase();

        log.info( payAgentPlatform.getName() + "回调签名:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            String shOrderId = ( String ) requestMap.get( "order_id" );

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( shOrderId );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", shOrderId );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", shOrderId );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( shOrderId );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, paysapiId, payAgentPlatform, "1".equals( status ) );
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
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put( "order_id", withdrawLog.getOrderNo() );
        paramsMap.put( "api_code", payAgentPlatform.getMerId() );
        paramsMap.put( "cash_money", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( paramsMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr ).toUpperCase();
        paramsMap.put( "sign", sign );


        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageForm( paramsMap ),
                null );

        log.info( payAgentPlatform.getName() + "查询结果- result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String returnCode = resultMap.getOrDefault( "return_code", 0 ).toString();
            if ( "SUCCESS".equals( returnCode ) ) {
                Map<String, Object> dataMap    = ( Map<String, Object> ) resultMap.get( "data" );
                int                 statusType = Integer.parseInt( dataMap.getOrDefault( "code", 0 ).toString() );
                // status 4代付中 5代付失败 6代付成功
                // statusType 0未处理，1成功，2失败，4处理中
                int status      = 4;
                int orderStatus = 0;
                if ( statusType == 1 ) {
                    status      = 6;
                    orderStatus = 1;
                } else if ( statusType == 2 ) {
                    status      = 5;
                    orderStatus = 2;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderStatus );
            }
            return resultMap.getOrDefault( "return_msg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
