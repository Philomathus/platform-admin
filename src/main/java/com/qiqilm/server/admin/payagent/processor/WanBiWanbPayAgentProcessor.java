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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.WANBI_WANB_PAY + "PayAgentProcessor" )
@Log4j2
public class WanBiWanbPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        String userId  = payAgentPlatform.getMerId();
        String amount  = String.valueOf( withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        String orderId = withdrawLog.getOrderNo();
        String account = withdrawLog.getBankAccount().trim();

        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "userid", userId );
        bodyMap.put( "amount", "0.00" );
        bodyMap.put( "orderid", orderId );
        bodyMap.put( "account", account );
        bodyMap.put( "notifyurl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put( "exrate", "0.00" );
        bodyMap.put( "examount", amount );
        bodyMap.put( "currency", "wanb" );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = userId + "0.00" + orderId + account + signMd5;

        log.warn( tempStr );

        String sign = DigestUtils.md5Hex( tempStr );
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
            reqPayAgent.setFailReason( payAgentPlatform.getName() + "代付下单报错原因:" + e );
        }
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String return_code = resultMap.getOrDefault( "code", "" ).toString();
            if ( "1".equals( return_code ) ) {
                log.info( payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "msg", "" ).toString() );
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn( payAgentPlatform.getName() + "代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String sign            = requestMap.remove( "sign" ).toString();
        String withdrawOrderId = requestMap.getOrDefault( "orderid", "" ).toString();
        String status          = requestMap.getOrDefault( "state", "" ).toString();
        String userId          = requestMap.getOrDefault( "userid", "" ).toString();
        String amount          = requestMap.getOrDefault( "amount", "" ).toString();
        String orderId         = requestMap.getOrDefault( "orderid", "" ).toString();

        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( withdrawOrderId );
        String            signMd5     = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = userId + amount + orderId + signMd5;
        String signStr = DigestUtils.md5Hex( tempStr );

        log.info( payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", withdrawOrderId );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 0 ) {
                log.error( "已有代付记录 - merOrderNo:{}", withdrawOrderId );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( withdrawOrderId );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, requestMap.getOrDefault( "id", "" )
                                                                                 .toString(), payAgentPlatform,
                    "4".equals( status ) );
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
        String            orderNo          = withdrawLog.getOrderNo();
        String            userid           = payAgentPlatform.getMerId();

        MultiValueMap<String, String> dataMap = new LinkedMultiValueMap<>();
        dataMap.add( "userid", userid );
        dataMap.add( "orderid", orderNo );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = userid + orderNo + signMd5;

        log.warn( tempStr );

        String sign = DigestUtils.md5Hex( tempStr );
        dataMap.add( "sign", sign );
        log.warn( payAgentPlatform.getName() + "查询代付状态接口请求参数{}", JsonUtil.object2Json( dataMap ) );

        UriComponents uriComponents = UriComponentsBuilder.fromUriString( payAgentPlatform.getPayOrderQueryAddr() )
                                                          .queryParams( dataMap ).build();

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( uriComponents.toUri(), HttpMethod.GET, restTemplate.httpEntityCallback( null ),
                    response -> {
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

        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String trade_state = resultMap.getOrDefault( "state", "" ).toString();
            if ( "4".equals( trade_state ) || "5".equals( trade_state ) || "7".equals( trade_state )
                    || "99".equals( trade_state ) ) {
                // status 4代付中 5代付失败 6代付成功
                // trade_state  4成功 5,99失败 7 處理中,需繼續查詢
                int status      = 4;
                int orderStatus = 0;
                if ( "4".equals( trade_state ) ) {
                    status      = 6;
                    orderStatus = 1;
                } else if ( "5".equals( trade_state ) || "99".equals( trade_state ) ) {
                    status      = 5;
                    orderStatus = 2;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderStatus );
            }
            return payAgentPlatform.getName() + "查询成功";
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
