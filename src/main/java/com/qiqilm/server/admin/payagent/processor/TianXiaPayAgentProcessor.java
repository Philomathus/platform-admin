package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeTianXiaType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.TIAN_XIA_PAY + "PayAgentProcessor" )
@Log4j2
public class TianXiaPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        BankCodeTianXiaType bankCodeType = BankCodeTianXiaType.getCodeByDesc( withdrawLog.getBankName() );
        if ( bankCodeType == null ) {
            log.warn( "代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName() );
            payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            throw new BusinessException( "此代付无法支持的银行类型：" + withdrawLog.getBankName() );
        }
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "merchant_id", payAgentPlatform.getMerId() );
        bodyMap.put( "merchant_order_id", withdrawLog.getOrderNo() );
        bodyMap.put( "user_level", "0" );
        bodyMap.put( "user_credit_level", "-9_9" );
        bodyMap.put( "pay_type", "912" );
        bodyMap.put( "pay_amt", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put( "return_url", "127.0.0.1" );
        bodyMap.put( "bank_code", bankCodeType.name() );
        bodyMap.put( "bank_num", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "bank_owner", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "bank_address", withdrawLog.getBankAddress() );
        bodyMap.put( "user_id", withdrawLog.getMemberId() );
        bodyMap.put( "user_ip", "127.0.0.1" );
        bodyMap.put( "member_account", withdrawLog.getMemberId() );
        bodyMap.put( "remark", "12345" );
        bodyMap.values().removeIf( v -> StringUtils.isBlank( v.toString() ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = String.format( "merchant_id=%s&merchant_order_id=%s&pay_type=%s&pay_amt=%s&notify_url=%s&return_url=%s"
                + "&bank_code=%s&bank_num=%s&bank_owner=%s&bank_address=%s&remark=%s&key=%s", bodyMap.get( "merchant_id" ),
                bodyMap.get( "merchant_order_id" ), bodyMap.get( "pay_type" ), bodyMap.get( "pay_amt" ), bodyMap.get(
                        "notify_url" ), bodyMap.get( "return_url" ), bodyMap.get( "bank_code" ), bodyMap.get( "bank_num" ),
                bodyMap.get( "bank_owner" ), bodyMap.get( "bank_address" ), bodyMap.get( "remark" ), signMd5 );
        log.warn( tempStr );
        String sign = DigestUtils.md5Hex( tempStr );

        log.warn( tempStr );
        log.warn( JsonUtil.object2Json( bodyMap ) );
        log.warn( "sign: {}", sign );

        Map<String, Object> resultMap = this.sendPostMap(
                payAgentPlatform.getPayOrderAddr() + "?sign=" + sign, packageForm( bodyMap ), reqPayAgent );
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "1".equals( resultMap.getOrDefault( "pay_message", "" ).toString() ) ) {
                log.info( payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "pay_result", "" ).toString() );
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

        String sign    = requestMap.remove( "sign" ).toString();
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );


        String tempStr = String.format( "merchant_id=%s&merchant_order_id=%s&typay_order_id=%s&pay_type=%s&pay_amt=%s"
                + "&pay_message=%s&remark=%s&key=%s", requestMap.get( "merchant_id" ), requestMap.get( "merchant_order_id" ),
                requestMap.get( "typay_order_id" ), requestMap.get( "pay_type" ), requestMap.get( "pay_amt" ), requestMap.get(
                        "pay_message" ), requestMap.get( "remark" ), signMd5 );
        log.warn( tempStr );
        String signStr = DigestUtils.md5Hex( tempStr );
        log.info( payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr );

        if ( sign.equalsIgnoreCase( signStr ) ) {
            String order_num = requestMap.getOrDefault( "merchant_order_id", "" ).toString();
            String status    = requestMap.getOrDefault( "pay_message", "" ).toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( order_num );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", order_num );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", order_num );
                return "200";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", order_num );
                return "200";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( order_num );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, order_num, payAgentPlatform, "1".equals( status ) );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "1".equals( status ) ? "成功" : "失败" );
            return "200";
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

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put( "merchant_id", payAgentPlatform.getMerId() );
        dataMap.put( "merchant_order_id", withdrawLog.getOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );

        log.warn( payAgentPlatform.getName() + "查询代付状态接口请求参数{}", JsonUtil.object2Json( dataMap ) );
        log.warn( "sign: {}", sign );
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( dataMap );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderQueryAddr() + "?sign="
                    + sign, HttpMethod.POST, restTemplate.httpEntityCallback( httpEntity ), response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return JsonUtil.json2Map( text );
            } );
            log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                //  status 4代付中 5代付失败 6代付成功
                int status = 4;
                //  支付结果，成功 1 、失败 -3或-2、处理中 -1 、查询失败 0、订单不存在 617
                String statusCode = resultMap.getOrDefault( "pay_message", "" ).toString();

                if ( "1".equals( statusCode ) ) {
                    status = 6;
                } else if ( "-2".equals( statusCode ) || "-3".equals( statusCode ) ) {
                    status = 5;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                        Integer.parseInt( statusCode ) );
                return "查询成功";
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return payAgentPlatform.getName() + "代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
