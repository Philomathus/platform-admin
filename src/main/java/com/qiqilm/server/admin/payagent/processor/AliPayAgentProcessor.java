package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AESCoder;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import com.qiqilm.server.admin.utils.UuidUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;

@Repository( value = ConstantsPayAgent.ALI_PAY + "PayAgentProcessor" )
@Log4j2
public class AliPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> payMap = new TreeMap<>();
        payMap.put( "busiAmount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.DOWN ) );
        payMap.put( "reqId", withdrawLog.getOrderNo() );
        payMap.put( "accountAddr", withdrawLog.getBankAccount() );
        payMap.put( "callbackUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        log.warn( JsonUtil.object2Json( payMap ) );

        String params = null;
        try {
            String signMd5       = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
            String signPublicKey = payAgentPlatform.getSignPublicKey();
            params = AESCoder.encryptBase64ByKeyIv( JsonUtil.object2Json( payMap ), signMd5, signPublicKey );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put( "pay", params );
        requestMap.put( "dc", payAgentPlatform.getMerId() );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        httpHeaders.set( "TraceId", withdrawLog.getMemberId() );
        httpHeaders.set( "uuid", UuidUtil.getRandomUuidWithoutSeparator() );
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );

        log.warn( JsonUtil.object2Json( httpEntity ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), httpEntity, reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "C2".equals( resultMap.getOrDefault( "code", "-1" ).toString() ) ) {
                Map<String, Object> result = ( Map<String, Object> ) resultMap.getOrDefault( "result", Collections.emptyMap() );
                String              status = result.getOrDefault( "status", "-1" ).toString();
                if ( "0".equals( status ) || "1".equals( status ) ) {
                    log.info( payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json( resultMap ) );
                    return true;
                } else {
                    reqPayAgent.setFailReason( resultMap.getOrDefault( "message", "" ).toString() );
                    payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
                }
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "message", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String merchantOrderNo = requestMap.getOrDefault( "reqId", "" ).toString();

        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( merchantOrderNo );
        if ( withdrawLog == null ) {
            log.error( "提现相关记录丢失 - merOrderNo:{}", merchantOrderNo );
            return "fail";
        }
        if ( withdrawLog.getStatus() == 2 ) {
            log.error( "订单已拒绝，无需回调 - merOrderNo:{}", merchantOrderNo );
            return "success";
        }
        if ( withdrawLog.getStatus() == 6 ) {
            log.error( "已有代付记录 - merOrderNo:{}", merchantOrderNo );
            return "success";
        }

        String resultString = requestMap.getOrDefault( "result", "" ).toString();

        try {
            String signMd5    = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
            String resultJson = AESCoder.decryptBase64ByKeyIv( resultString, signMd5, payAgentPlatform.getSignPublicKey() );
            log.warn( resultJson );
            Map<String, Object> resultMap = JsonUtil.json2Map( resultJson );
            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( merchantOrderNo );

                boolean isSuccess = "1".equals( resultMap.getOrDefault( "status", "-1" ).toString() );

                payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, isSuccess );
                log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", merchantOrderNo, isSuccess ? "成功" : "失败" );
                return "success";
            }
            return "fail";
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
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

        SortedMap<String, Object> payMap = new TreeMap<>();
        payMap.put( "reqId", withdrawLog.getOrderNo() );

        log.warn( JsonUtil.object2Json( payMap ) );

        String params = null;
        try {
            String signMd5       = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
            String signPublicKey = payAgentPlatform.getSignPublicKey();
            params = AESCoder.encryptBase64ByKeyIv( JsonUtil.object2Json( payMap ), signMd5, signPublicKey );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put( "pay", params );
        requestMap.put( "dc", payAgentPlatform.getMerId() );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        httpHeaders.set( "TraceId", withdrawLog.getMemberId() );
        httpHeaders.set( "uuid", UuidUtil.getRandomUuidWithoutSeparator() );
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), httpEntity, null );

        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "C2".equals( resultMap.getOrDefault( "code", "-1" ).toString() ) ) {
                Map<String, Object> result     = ( Map<String, Object> ) resultMap.getOrDefault( "result",
                        Collections.emptyMap() );
                int                 statusCode = Integer.parseInt( result.getOrDefault( "status", -1 ).toString() );
                //  0待支付1已完成2失败
                int status;
                switch ( statusCode ) {
                case 1:
                    status = 6;
                    break;
                case 2:
                    status = 5;
                    break;
                default:
                    status = 4;
                    break;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, 0 );

            }
            return resultMap.getOrDefault( "message", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
