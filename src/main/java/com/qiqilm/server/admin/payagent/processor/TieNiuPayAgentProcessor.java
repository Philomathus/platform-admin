package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.*;
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

@Repository( value = ConstantsPayAgent.TIE_NIU + "PayAgentProcessor" )
@Log4j2
public class TieNiuPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "mchid", payAgentPlatform.getMerId() );
        dataMap.put( "order", withdrawLog.getOrderNo() );
        dataMap.put( "money", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).intValue() );
        dataMap.put( "name", withdrawLog.getBankUserName().trim() );
        dataMap.put( "card", withdrawLog.getBankAccount().trim() );
        dataMap.put( "bank_name", withdrawLog.getBankName() );
        dataMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        dataMap.put( "time", DateFormatUtils.formate( new Date() ) );
        dataMap.put( "client_ip", "192.168.0.1" );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );
        dataMap.put( "sign", sign );

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( dataMap );
        log.warn( payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json( requestMap ) );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );

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
        }
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "0".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                log.info( payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "msg", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "fail ip";
        }
        String                    rspSign = requestMap.remove( "sign" ).toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );

        log.info( payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign );
        if ( rspSign.equalsIgnoreCase( sign ) ) {
            String order_num = requestMap.getOrDefault( "order", "" ).toString();
            String status    = requestMap.getOrDefault( "finish_status", "" ).toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( order_num );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", order_num );
                return "fail null";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", order_num );
                return "OK";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( order_num );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "1".equals( status ) );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "1".equals( status ) ? "成功" : "失败" );
            return "OK";
        }
        return "fail sign";
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
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "mchid", payAgentPlatform.getMerId() );
        dataMap.put( "order", withdrawLog.getOrderNo() );
        dataMap.put( "type", "transfer" );
        dataMap.put( "time", DateFormatUtils.formate( new Date() ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        dataMap.put( "sign", DigestUtils.md5Hex( tempStr ) );

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( dataMap );
        log.warn( payAgentPlatform.getName() + "查询代付状态接口请求参数{}", JsonUtil.object2Json( requestMap ) );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );

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
            log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                Map<String, Object> dataResMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
                if ( !CollectionUtils.isEmpty( dataResMap ) ) {
                    //  status 4代付中 5代付失败 6代付成功
                    int status;
                    //  statusCode 0-待处理,1-处理中,2-成功,3-失败
                    String statusCode = dataResMap.getOrDefault( "finish_status", "" ).toString();

                    switch ( statusCode ) {
                    case "1":
                        status = 6;
                        break;
                    case "2":
                    case "3":
                        status = 5;
                        break;
                    default:
                        status = 4;
                    }
                    if ( status > 4 ) {
                        payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                                Integer.parseInt( statusCode ) );
                    }
                }
                return resultMap.getOrDefault( "msg", "" ).toString();
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
