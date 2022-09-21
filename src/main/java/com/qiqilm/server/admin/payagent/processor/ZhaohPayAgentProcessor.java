package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeZhaoHType;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
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
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Repository( value = ConstantsPayAgent.ZHAOH + "PayAgentProcessor" )
@Log4j2
public class ZhaohPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        BankCodeZhaoHType bankCodeType = BankCodeZhaoHType.getCodeByDesc( withdrawLog.getBankName() );
        if ( bankCodeType == null ) {
            bankCodeType = BankCodeZhaoHType.OTHER;
        }
        withdrawLog.setBankCode( bankCodeType.name() );

        Map<String, String> dataMap = new TreeMap<>();
        dataMap.put( "merchantCode", payAgentPlatform.getMerId() );
        dataMap.put( "merchantNo", withdrawLog.getOrderNo() );
        dataMap.put( "amount", withdrawLog.getWithdrawMoney().setScale( 0, RoundingMode.HALF_UP ).toString() );
        dataMap.put( "coinUnit", "CNY" );
        dataMap.put( "callbackDataFormat", "JSON" );
        dataMap.put( "name", withdrawLog.getBankUserName().trim() );
        dataMap.put( "bankNo", withdrawLog.getBankAccount().trim() );
        dataMap.put( "issueBankCode", withdrawLog.getBankCode() );
        String callbackUrl = sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode();
        dataMap.put( "callbackUrl", Base64Utils.encodeToUrlSafeString( callbackUrl.getBytes( StandardCharsets.UTF_8 ) ) );
        dataMap.put( "channelGroup", "0" );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey" + "/payAgentPrivateKey" ) );

        String tempStr = this.assemblyUrl( dataMap ) + "&token=" + payAgentPlatform.getHeaderKey() + "&sign=" + signMd5;
        log.warn( tempStr );
        String sign = DigestUtils.md5Hex( tempStr );
        dataMap.put( "sign", sign );

        log.warn( JsonUtil.object2Json( dataMap ) );

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put( "data", RSACoder.encryptByPublicKey( this.assemblyUrl( dataMap ), payAgentPlatform.getSignPublicKey() ) );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        httpHeaders.set( "token", payAgentPlatform.getHeaderKey() );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                log.warn( text );
                return JsonUtil.json2Map( text );
            } );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
            reqPayAgent.setFailReason( payAgentPlatform.getName() + "下单报错原因:" + e.getMessage() );
        }
        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String dataStr = resultMap.getOrDefault( "data", "" ).toString();
            Map<String, Object> resDataMap = JsonUtil.json2Map( RSACoder.decryptByPublicKey( dataStr,
                    payAgentPlatform.getSignPublicKey() ) );
            log.warn( "解密数据:" + JsonUtil.object2Json( resDataMap ) );
            if ( "0".equals( resDataMap.getOrDefault( "code", "" ).toString() ) ) {
                Map<String, Object> data = ( Map<String, Object> ) resDataMap.getOrDefault( "data", new HashMap<>() );
                if ( "SUCCESS".equals( data.getOrDefault( "result", "" ) ) ) {
                    log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                    return true;
                }
            }
            reqPayAgent.setFailReason( resDataMap.getOrDefault( "message", "" ).toString() );
            payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "fail";
        }

        String orderNo = requestMap.getOrDefault( "orderNo", "" ).toString();
        String dataStr = requestMap.getOrDefault( "data", "" ).toString();
        Map<String, Object> dataMap = JsonUtil.json2Map( RSACoder.decryptByPrivateKey( dataStr,
                payAgentPlatform.getSignPrivateKey() ) );

        // 解密后对签名验证
        SortedMap<String, Object> signMap = new TreeMap<>( dataMap );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey" + "/payAgentPrivateKey" ) );

        String sign    = signMap.remove( "sign" ).toString();
        String tempStr = this.assemblyUrl( signMap ) + "&token=" + payAgentPlatform.getHeaderKey() + "&sign=" + signMd5;

        if ( StringUtils.equalsIgnoreCase( DigestUtils.md5Hex( tempStr ), sign ) ) {
            int status = Integer.parseInt( dataMap.getOrDefault( "status", -1 ).toString() );
            if ( status == 2 || status == 3 ) {
                MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderNo );
                if ( withdrawLog == null ) {
                    log.error( "提现相关记录丢失 - OrderNo:{}", orderNo );
                    return "fail";
                }
                if ( withdrawLog.getStatus() == 6 ) {
                    log.error( "已有代付记录 - OrderNo:{}", orderNo );
                    return "success";
                }
                PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderNo );
                payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, status == 2 );
                log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", orderNo, status == 2 ? "成功" : "失败" );
            }
            return "success";
        }
        log.info( payAgentPlatform.getName() + "回调验签失败" );
        return "fail";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                     String realIp ) throws Exception {
        Map<String, Object> resultMap = new TreeMap<>();
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            resultMap.put( "message", "请求ip非白名单:" + realIp );
            resultMap.put( "code", 9999 );
            return resultMap;
        }
        Map<String, Object> dataMap;
        if ( requestMap.get( "data" ) instanceof String ) {
            dataMap = JsonUtil.json2Map( requestMap.get( "data" ).toString() );
        } else {
            dataMap = ( Map<String, Object> ) requestMap.get( "data" );
        }

        // 解密后对签名验证
        SortedMap<String, Object> signMap = new TreeMap<>( dataMap );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey" + "/payAgentPrivateKey" ) );

        String sign    = signMap.remove( "sign" ).toString();
        String tempStr = this.assemblyUrl( signMap ) + "&token=" + payAgentPlatform.getHeaderKey() + "&sign=" + signMd5;

        if ( StringUtils.equalsIgnoreCase( DigestUtils.md5Hex( tempStr ), sign ) ) {
            String            orderNo           = requestMap.get( "merOrderNo" ).toString();
            String            bankAccountNo     = requestMap.get( "bankAccountNo" ).toString();
            String            merId             = requestMap.get( "merId" ).toString();
            BigDecimal        amount            = new BigDecimal( requestMap.get( "amount" ).toString() );
            MemberWithdrawLog memberWithdrawLog = withdrawLogMapper.selectByOrderNo( orderNo );
            if ( memberWithdrawLog == null ) {
                resultMap.put( "message", "订单不存在" );
                resultMap.put( "code", 1002 );
                return resultMap;
            }
            if ( amount.compareTo( memberWithdrawLog.getWithdrawMoney() ) != 0 ) {
                signMap.put( "code", 1004 );
                signMap.put( "message", "金额不匹配" );
                return resultMap;
            }
            if ( !bankAccountNo.equals( memberWithdrawLog.getBankAccount() ) ) {
                signMap.put( "code", 1003 );
                signMap.put( "message", "银行卡不匹配" );
                return resultMap;
            }
            if ( !merId.equals( payAgentPlatform.getMerId() ) ) {
                signMap.put( "code", 9999 );
                signMap.put( "message", "商户号错误" );
                return resultMap;
            }
            resultMap.put( "code", 0 );
            resultMap.put( "message", "验证成功" );
            resultMap.put( "merId", merId );
            resultMap.put( "merOrderNo", orderNo );
            String signRes = this.assemblyUrl( resultMap ) + "&token=" + payAgentPlatform.getHeaderKey() + "&sign=" + signMd5;
            resultMap.put( "sign", DigestUtils.md5Hex( signRes ) );
            return resultMap;
        }
        resultMap.put( "message", "验签失败" );
        resultMap.put( "code", 1001 );
        return resultMap;
    }

    @Override
    public String queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
        MemberWithdrawLog   withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform    payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
        Map<String, String> dataMap          = new TreeMap<>();
        dataMap.put( "merchantCode", payAgentPlatform.getMerId() );
        dataMap.put( "merchantNo", withdrawLog.getOrderNo() );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey" + "/payAgentPrivateKey" ) );

        // 生成签名信息
        String signStr = this.assemblyUrl( dataMap ) + "&token=" + payAgentPlatform.getHeaderKey() + "&sign=" + signMd5;
        String sign    = DigestUtils.md5Hex( signStr );
        dataMap.put( "sign", sign );

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put( "data", RSACoder.encryptByPublicKey( this.assemblyUrl( dataMap ), payAgentPlatform.getSignPublicKey() ) );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );

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
        log.info( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "0".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                Map<String, Object> resultDataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
                int                 orderState    = Integer.parseInt( resultDataMap.getOrDefault( "Status", 0 ).toString() );
                // status 4代付中5代付失败6代付成功
                // orderState 2 成功 3 失败 1 处理中
                int status = 4;
                switch ( orderState ) {
                case 2:
                    status = 6;
                    break;
                case 3:
                    status = 5;
                    break;
                default:
                    break;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState );
            }
            return resultMap.getOrDefault( "message", "" ).toString();
        }
        return payAgentPlatform.getName() + "代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
