package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeLPType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository( value = ConstantsPayAgent.LP + "PayAgentProcessor" )
@Log4j2
public class LPPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        BankCodeLPType bankCodeType = BankCodeLPType.getCodeByDesc( withdrawLog.getBankName() );
        if ( bankCodeType == null ) {
            payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            log.warn( "此代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName() );
            throw new BusinessException( "此代付无法支持的银行类型：" + withdrawLog.getBankName() );
        }

        Map<String, String> bodyMap = new LinkedHashMap<>();
        bodyMap.put( "merchant_no", payAgentPlatform.getMerId() );
        bodyMap.put( "order_no", withdrawLog.getOrderNo() );
        bodyMap.put( "card_no", withdrawLog.getBankAccount() );
        bodyMap.put( "account_name", Base64Utils.encodeToString( withdrawLog.getBankUserName()
                                                                            .getBytes( StandardCharsets.UTF_8 ) ) );
        bodyMap.put( "bank_branch", "" );
        bodyMap.put( "cnaps_no", "" );
        bodyMap.put( "bank_code", bankCodeType.name() );
        bodyMap.put( "bank_name", Base64Utils.encodeToString( withdrawLog.getBankName().getBytes( StandardCharsets.UTF_8 ) ) );
        bodyMap.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).toString() );
        bodyMap.put( "backend_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + "&pay_pwd=" + payAgentPlatform.getHeaderKey() + "&key=" + signMd5;
        log.warn( tempStr );

        bodyMap.put( "sign", DigestUtils.md5Hex( tempStr ) );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( bodyMap );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );

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
            reqPayAgent.setFailReason( payAgentPlatform.getName() + "代付下单报错原因:" + e );
        }
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String resultCode = resultMap.getOrDefault( "result_code", "" ).toString();
            if ( "000000".equals( resultCode ) ) {
                log.info( payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "result_msg", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "fail";
        }

        String sign   = requestMap.remove( "sign" ).toString();
        String status = requestMap.getOrDefault( "result", "" ).toString();

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = JsonUtil.object2Json( requestMap ) + signMd5;
        log.warn( "回调验签加密前字符串:" + tempStr );
        String signStr = DigestUtils.md5Hex( tempStr );

        log.info( payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            List<Map<String, String>> orders = ( List<Map<String, String>> ) requestMap.getOrDefault( "orders",
                    new ArrayList<>() );
            Map<String, String> orderMap    = orders.get( 0 );
            String              merOrderNo  = orderMap.get( "mer_order_no" );
            MemberWithdrawLog   withdrawLog = withdrawLogMapper.selectByOrderNo( merOrderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", merOrderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 0 ) {
                log.error( "已有代付记录 - merOrderNo:{}", merOrderNo );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( merOrderNo );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, orderMap.getOrDefault( "order_no", "" ),
                    payAgentPlatform, "S".equals( status ) );
            return "SUCCESS";
        }
        return "fail";
    }

    public String reverseCheckOrderPayStr( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "ERROR";
        }

        String sign       = requestMap.remove( "sign" ).toString();
        String merOrderNo = requestMap.getOrDefault( "mer_order_no", "" ).toString();
        String merchantNo = requestMap.getOrDefault( "merchant_no", "" ).toString();
        String cardNo     = requestMap.getOrDefault( "card_no", "" ).toString();
        String amount     = requestMap.getOrDefault( "amount", "0" ).toString();

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put( "merchant_no", requestMap.get( "merchant_no" ) );
        dataMap.put( "mer_order_no", merOrderNo );
        dataMap.put( "card_no", requestMap.get( "card_no" ) );
        dataMap.put( "amount", requestMap.get( "amount" ) );
        dataMap.put( "key", signMd5 );

        String signStr = DigestUtils.md5Hex( this.assemblyUrl( dataMap ) );
        if ( signStr.equalsIgnoreCase( sign ) ) {
            MemberWithdrawLog memberWithdrawLog = withdrawLogMapper.selectByOrderNo( merOrderNo );
            if ( memberWithdrawLog == null ) {
                log.error( "LP反查订单不存在, 订单ID:{}", merOrderNo );
                return "ERROR";
            } else if ( new BigDecimal( amount ).compareTo( memberWithdrawLog.getWithdrawMoney() ) != 0 ) {
                log.error( "LP反查金额不匹配, 订单ID:{}", merOrderNo );
                return "ERROR";
            } else if ( !cardNo.equals( memberWithdrawLog.getBankAccount() ) ) {
                log.error( "LP反查银行卡号不匹配, 订单ID:{}", merOrderNo );
                return "ERROR";
            } else if ( !merchantNo.equals( payAgentPlatform.getMerId() ) ) {
                log.error( "LP反查商户号错误, 订单ID:{}", merOrderNo );
                return "ERROR";
            } else {
                return "SUCCESS";
            }
        }
        log.error( "LP反查签名错误, 订单ID:{}", merOrderNo );
        return "ERROR";
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

        Map<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put( "merchant_no", payAgentPlatform.getMerId() );
        dataMap.put( "order_no", withdrawLog.getOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;

        String sign = DigestUtils.md5Hex( tempStr );
        dataMap.put( "sign", sign );
        log.warn( payAgentPlatform.getName() + "查询代付状态接口请求参数{}", JsonUtil.object2Json( dataMap ) );

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( dataMap );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );


        try {
            Map<String, Object> resultMap = restTemplate.execute( payAgentPlatform.getPayOrderQueryAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return JsonUtil.json2Map( text );
            } );
            log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                String resultCode = resultMap.getOrDefault( "result_code", "" ).toString();
                if ( "000000".equals( resultCode ) ) {
                    String tradeState = resultMap.getOrDefault( "result", "" ).toString();
                    if ( "S".equals( tradeState ) || "F".equals( tradeState ) || "H".equals( tradeState ) ) {
                        // status 4代付中 5代付失败 6代付成功
                        // tradeState  100成功 -90失败 0 處理中,需繼續查詢
                        int status      = 4;
                        int orderStatus = 0;
                        if ( "S".equals( tradeState ) ) {
                            status      = 6;
                            orderStatus = 1;
                        } else if ( "F".equals( tradeState ) ) {
                            status      = 5;
                            orderStatus = 2;
                        }
                        payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                                orderStatus );
                    }
                }
                return resultMap.getOrDefault( "result_msg", "" ).toString();
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
