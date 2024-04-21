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
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.AFGHAN + "PayAgentProcessor" )
@Log4j2
public class AfghanPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "mchNo", payAgentPlatform.getMerId() );
        bodyMap.put( "appId", payAgentPlatform.getHeaderKey() );
        bodyMap.put( "mchOrderNo", withdrawLog.getOrderNo() );
        bodyMap.put( "entryType", "BANK_CARD" );
        bodyMap.put( "amount", withdrawLog
                .getWithdrawMoney()
                .multiply( new BigDecimal( 100 ) )
                .setScale( 0, RoundingMode.HALF_UP ) );
        bodyMap.put( "currency", "cny" );
        bodyMap.put( "accountNo", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "accountName", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "bankName", withdrawLog.getBankName().trim() );
        bodyMap.put( "clientIp", "127.0.0.1" );
        bodyMap.put( "transferDesc", "无" );
        bodyMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put( "reqTime", System.currentTimeMillis() );
        bodyMap.put( "version", "1.0" );
        bodyMap.put( "signType", "MD5" );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        log.warn( tempStr );
        bodyMap.put( "sign", DigestUtils.md5Hex( tempStr ).toUpperCase() );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String              retCode = resultMap.getOrDefault( "code", "" ).toString();
            Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", Collections.emptyMap() );
            if ( "0".equals( retCode ) ) {
                log.info( payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( !CollectionUtils.isEmpty( dataMap ) ? dataMap
                        .getOrDefault( "errMsg", "" )
                        .toString() : resultMap.getOrDefault( "msg", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String sign   = requestMap.remove( "sign" ).toString();
        int    status = Integer.parseInt( requestMap.getOrDefault( "state", "-1" ).toString() );

        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String rspSign = DigestUtils.md5Hex( tempStr );

        log.info( payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign );
        if ( sign.equalsIgnoreCase( rspSign ) ) {
            String mchOrderNo = ( String ) requestMap.get( "mchOrderNo" );

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( mchOrderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", mchOrderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", mchOrderNo );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( mchOrderNo );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, status == 2 );

            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", mchOrderNo, status == 2 ? "成功" : "失败" );
            return "success";
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
        paramsMap.put( "mchNo", payAgentPlatform.getMerId() );
        paramsMap.put( "appId", payAgentPlatform.getHeaderKey() );
        paramsMap.put( "mchOrderNo", withdrawLog.getOrderNo() );
        paramsMap.put( "reqTime", System.currentTimeMillis() );
        paramsMap.put( "version", "1.0" );
        paramsMap.put( "signType", "MD5" );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( paramsMap ) + "&key=" + signMd5;
        paramsMap.put( "sign", DigestUtils.md5Hex( tempStr ).toUpperCase() );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( paramsMap ),
                null );

        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String              retCode = resultMap.getOrDefault( "code", "" ).toString();
            Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", Collections.emptyMap() );
            if ( "0".equals( retCode ) ) {
                int statusType = Integer.parseInt( dataMap.getOrDefault( "state", "-1" ).toString() );
                //  4代付中 5代付失败 6代付成功
                int status;
                int orderStatus;
                switch ( statusType ) {
                case 2:
                    status = 6;
                    orderStatus = 1;
                    break;
                case 0:
                case 1:
                    status = 4;
                    orderStatus = 0;
                    break;
                default:
                    status = 5;
                    orderStatus = 2;
                    break;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderStatus );
            }
            return resultMap.getOrDefault( "msg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
