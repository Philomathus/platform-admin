package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.util.*;

@Repository( value = ConstantsPayAgent.Ma_Yun + "PayAgentProcessor" )
@Log4j2
public class MaYunPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {

        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "merchant_sn", payAgentPlatform.getMerId() );
        bodyMap.put( "merchant_order_sn", withdrawLog.getOrderNo() );
        bodyMap.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "code", "1" );
        bodyMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put( "attach", "hahaha" );
        bodyMap.put( "timestamp", System.currentTimeMillis() / 1000 );
        bodyMap.put( "name", withdrawLog.getBankUserName() );
        bodyMap.put( "account", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "bank", withdrawLog.getBankName().trim() );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( signStr ).toUpperCase();
        bodyMap.put( "sign", sign );

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( bodyMap );
        log.warn( JsonUtil.object2Json( requestMap ) );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );

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
            reqPayAgent.setFailReason( e.getMessage() );
        }
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "1".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                log.info( "马云代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "msg", "" ).toString() );

                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( "马云代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
        return false;
    }

    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "fail";
        }
        String merchantOrderSn = requestMap.get( "merchant_order_sn" ).toString();
        String sign            = requestMap.remove( "sign" ).toString();

        SortedMap<String, Object> signMap = new TreeMap<>( requestMap );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = this.assemblyUrl( signMap ) + "&key=" + signMd5;
        log.info( signStr );
        String mySign = DigestUtils.md5Hex( signStr ).toUpperCase();
        if ( org.apache.commons.lang3.StringUtils.equalsIgnoreCase( sign, mySign ) ) {
            String            status      = signMap.getOrDefault( "status", "" ).toString();
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( merchantOrderSn );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", merchantOrderSn );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", merchantOrderSn );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( merchantOrderSn );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, merchantOrderSn, payAgentPlatform, "2".equals( status ) );
            log.info( payAgentPlatform.getName()
                    + "订单号:{},回调状态:{},", merchantOrderSn, "2".equals( status ) ? "成功" : "失败" );
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

        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "merchant_sn", payAgentPlatform.getMerId() );
        bodyMap.put( "merchant_order_sn", withdrawLog.getOrderNo() );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        // 生成签名信息
        String signStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( signStr ).toUpperCase();
        bodyMap.put( "sign", sign );

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( bodyMap );
        log.warn( JsonUtil.object2Json( requestMap ) );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );

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
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        log.warn( "马云代付查询结果" + JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "1".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                Map dataMap    = ( Map ) resultMap.getOrDefault( "data", "" );
                int status1    = Integer.parseInt( dataMap.getOrDefault( "status", "" ).toString() );
                int status     = 4;
                int orderState = 0;
                // status 4代付中 5代付失败 6代付成功
                // statusType 0：待处理， 1：处理中， 2：已打款， 3：已拒绝 ， 4：已退单
                switch ( status1 ) {
                case 2:
                    status = 6;
                    break;
                case 3:
                case 4:
                    status = 5;
                    break;
                default:
                    break;
                }

                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState );
            }
            return resultMap.getOrDefault( "msg", "" ).toString();
        }
        return "马云代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
