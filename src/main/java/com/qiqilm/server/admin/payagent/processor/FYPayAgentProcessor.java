package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository ( value = ConstantsPayAgent.FY_PAY + "PayAgentProcessor" )
@Log4j2
public class FYPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "merchId", payAgentPlatform.getMerId() );
        bodyMap.put( "outTradeNo", withdrawLog.getOrderNo() );
        bodyMap.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "accBankName", withdrawLog.getBankName().trim() );
        bodyMap.put( "accName", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "accNo", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        log.warn( tempStr );
        String sign = DigestUtils.md5Hex( tempStr ).toUpperCase();
        bodyMap.put( "sign", sign );

        log.warn( JsonUtil.object2Json( bodyMap ) );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( bodyMap, httpHeaders );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                        InputStream bodyStream = response.getBody();
                        String      text;
                        try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                            text = CharStreams.toString( reader );
                        }
                        return JsonUtil.json2Map( text );
                    } );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
            reqPayAgent.setFailReason( payAgentPlatform.getName() + "下单报错原因:" + e );

            if ( e.getMessage().contains( "failed to respond" ) ) {
                reqPayAgent.setFailReason( "三方网络异常:" + e.getMessage() );
                payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
                return false;
            }
        }
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "success".equals( resultMap.getOrDefault( "message", "" ).toString() )
                    && resultMap.get( "data" ) != null ) {
                log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "message", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String signRes = requestMap.remove( "sign" ).toString();

        Map<String, Object> dataMap = new TreeMap<>( requestMap );
        String              signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );
        if ( signRes.equalsIgnoreCase( sign ) ) {
            String            withdrawOrderId = dataMap.getOrDefault( "outTradeNo", "" ).toString();
            String            status          = dataMap.getOrDefault( "status", "" ).toString();
            MemberWithdrawLog withdrawLog     = withdrawLogMapper.selectByOrderNo( withdrawOrderId );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", withdrawOrderId );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 0 ) {
                log.error( "已有代付记录 - merOrderNo:{}", withdrawOrderId );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( withdrawOrderId );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, requestMap.getOrDefault( "order_no", "" )
                            .toString(), payAgentPlatform,
                    "OK_ORDER".equals( status ) );
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
        MemberWithdrawLog   withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform    payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
        Map<String, Object> dataMap          = new TreeMap<>();
        dataMap.put( "outTradeNo", withdrawLog.getOrderNo() );
        dataMap.put( "merchId", payAgentPlatform.getMerId() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        dataMap.put( "sign", DigestUtils.md5Hex( tempStr ).toUpperCase() );

        log.warn( JsonUtil.object2Json( dataMap ) );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( dataMap, httpHeaders );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderQueryAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                        InputStream bodyStream = response.getBody();
                        String      text;
                        try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                            text = CharStreams.toString( reader );
                        }
                        return JsonUtil.json2Map( text );
                    } );
            log.info( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );
            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                String              success    = resultMap.getOrDefault( "message", "" ).toString();
                Map<String, Object> dataResMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
                if ( "success".equals( success ) && !CollectionUtils.isEmpty( dataResMap ) ) {
                    String refCode = dataResMap.getOrDefault( "status", "" ).toString();
                    // status 4代付中 5代付失败 6代付成功
                    // refCode 1成功 2失败 3处理中 4待处理
                    int status     = 4;
                    int orderState = 0;
                    if ( "OK_ORDER".equals( refCode ) ) {
                        status = 6;
                        orderState = 1;
                    } else if ( "CLOSE_ORDER".equals( refCode ) ) {
                        status = 5;
                        orderState = 2;
                    }
                    payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState );
                }
                return resultMap.getOrDefault( "message", "" ).toString();
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
