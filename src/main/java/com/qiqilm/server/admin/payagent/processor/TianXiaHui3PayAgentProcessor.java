package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeTianXiaHuiType;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.DateFormatUtils;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.TIANXIAHUI3_PAY + "PayAgentProcessor" )
@Log4j2
public class TianXiaHui3PayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        BankCodeTianXiaHuiType    bankCodeType = BankCodeTianXiaHuiType.getCodeByDesc( withdrawLog.getBankName() );
        SortedMap<String, Object> bodyMap      = new TreeMap<>();
        bodyMap.put( "bankName", withdrawLog.getBankName() );
        if ( bankCodeType == null ) {
            bodyMap.put( "bankNumber", "其他银行" );
            bodyMap.put( "bankName", "其他银行" );
        } else {
            bodyMap.put( "bankNumber", bankCodeType.name() );
            bodyMap.put( "bankName", bankCodeType.getDesc().get( 0 ) );
        }
        bodyMap.put( "mchId", payAgentPlatform.getMerId() );
        bodyMap.put( "mchOrderNo", withdrawLog.getOrderNo() );
        bodyMap.put( "amount", withdrawLog.getWithdrawMoney().multiply( BigDecimal.valueOf( 100 ) )
                .setScale( 0, RoundingMode.HALF_UP ).intValue() );
        bodyMap.put( "accountName", withdrawLog.getBankUserName() );
        bodyMap.put( "accountNo", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "remark", withdrawLog.getOrderNo() );
        bodyMap.put( "reqTime", DateFormatUtils.formate( new Date(), "yyyyMMddHHmmss" ) );
        bodyMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );
        bodyMap.put( "sign", sign );

        log.warn( tempStr );
        log.warn( JsonUtil.object2Json( bodyMap ) );

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( bodyMap );
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
            reqPayAgent.setFailReason( payAgentPlatform.getName() + "代付下单报错原因:" + e );
        }
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String return_code = resultMap.getOrDefault( "retCode", "" ).toString();
            if ( "SUCCESS".equals( return_code ) ) {
                log.info( payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "errDes", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String orderNo = requestMap.getOrDefault( "mchOrderNo", "" ).toString();
        String status  = requestMap.getOrDefault( "status", "" ).toString();
        String sign    = requestMap.remove( "sign" ).toString();
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );
        bodyMap.remove( "remark" );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex( tempStr );
        bodyMap.put( "sign", signStr );
        log.info( payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr );

        if ( sign.equalsIgnoreCase( signStr ) ) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", orderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "已有代付记录 - merOrderNo:{}", orderNo );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderNo );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, orderNo, payAgentPlatform, "2".equals( status ) );
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

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "mchId", payAgentPlatform.getMerId() );
        dataMap.put( "mchOrderNo", withdrawLog.getOrderNo() );
        dataMap.put( "reqTime", DateFormatUtils.formate( new Date(), "yyyyMMddHHmmss" ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );
        dataMap.put( "sign", sign );
        log.warn( tempStr );

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( dataMap );
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
                String return_code = resultMap.getOrDefault( "retCode", "" ).toString();
                if ( "SUCCESS".equals( return_code ) ) {
                    String statusCode = resultMap.getOrDefault( "status", "" ).toString();

                    // status 4代付中5代付失败6代付成功
                    // orderState 0 回调中 1 成功 2失败
                    int status     = 4;
                    int orderState = 0;
                    if ( "2".equals( statusCode ) ) {
                        status     = 6;
                        orderState = 1;
                    } else if ( "3".equals( statusCode ) ) {
                        status     = 5;
                        orderState = 2;
                    }
                    payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                            orderState );
                    return "查询成功";
                }
                return "查询失败";
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return payAgentPlatform.getName() + "代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}

