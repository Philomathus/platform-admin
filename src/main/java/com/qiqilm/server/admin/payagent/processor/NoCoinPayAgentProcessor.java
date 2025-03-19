package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.NO_COIN_PAY + "PayAgentProcessor" )
@Log4j2
public class NoCoinPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        if ( !"USDT".equalsIgnoreCase( withdrawLog.getBankCode() ) ) {
            payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            log.warn( "此代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName() );
            throw new BusinessException( "此代付无法支持的银行类型：" + withdrawLog.getBankName() );
        }
        Map<String, Object> dataMap                  = new TreeMap<>();
        BigDecimal          usdtWithdrawExchangeRate = sysConfigCacheUtil.getConfBd( "usdt_withdraw_exchange_rate" );
        dataMap.put( "appId", payAgentPlatform.getMerId() );
        dataMap.put( "merchantOrderNo", withdrawLog.getOrderNo() );
        dataMap.put( "merchantMemberNo", withdrawLog.getMemberId() );
        dataMap.put( "amount", withdrawLog.getWithdrawMoney().divide( usdtWithdrawExchangeRate, 6, RoundingMode.HALF_DOWN ) );
        dataMap.put( "rate", usdtWithdrawExchangeRate.stripTrailingZeros().toPlainString() );
        dataMap.put( "language", "zh" );
        dataMap.put( "coin", "USDT" );
        dataMap.put( "protocol", "TRC20" );
        dataMap.put( "rateType", 1 );
        dataMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        dataMap.put( "toAddress", withdrawLog.getBankAccount() );
        dataMap.put( "timestamp", System.currentTimeMillis() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        log.warn( signStr );
        dataMap.put( "sign", DigestUtils.sha256Hex( signStr ) );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set( "version", "V1" );
        httpHeaders.set( "appId", payAgentPlatform.getMerId() );
        httpHeaders.set( "language", "zh_CN" );
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( dataMap, httpHeaders );

        log.warn( payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json( dataMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), httpEntity, reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "code", "" ).toString();
            if ( "0".equals( code ) ) {
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

        String merchantOrderNo = requestMap.getOrDefault( "merchantOrderNo", "" ).toString();
        String state           = requestMap.getOrDefault( "state", "" ).toString();
        String sign            = requestMap.remove( "sign" ).toString();

        Map<String, Object> treeMap = new TreeMap<>( requestMap );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = this.assemblyUrl( treeMap ) + "&key=" + signMd5;
        String tempStr = DigestUtils.sha256Hex( signStr );

        log.info( payAgentPlatform.getName() + "回调签名字符串:" + sign + "_" + tempStr );
        if ( tempStr.equalsIgnoreCase( sign ) ) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( merchantOrderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", merchantOrderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", merchantOrderNo );
                return "SUCCESS";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", merchantOrderNo );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( merchantOrderNo );
            boolean     isSuccess   = "3".equals( state );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, isSuccess );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", merchantOrderNo, isSuccess ? "成功" : "失败" );
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

        Map<String, Object> params = new TreeMap<>();
        params.put( "appId", payAgentPlatform.getMerId() );
        params.put( "merchantOrderNo", withdrawLog.getOrderNo() );
        params.put( "merchantMemberNo", withdrawLog.getMemberId() );
        params.put( "timestamp", System.currentTimeMillis() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = this.assemblyUrl( params ) + "&key=" + signMd5;
        log.warn( signStr );
        params.put( "sign", DigestUtils.sha256Hex( signStr ) );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set( "version", "V1" );
        httpHeaders.set( "appId", payAgentPlatform.getMerId() );
        httpHeaders.set( "language", "zh_CN" );
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( params, httpHeaders );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), httpEntity, null );

        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String              code    = resultMap.getOrDefault( "code", "" ).toString();
            Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.get( "data" );
            if ( "0".equals( code ) && !CollectionUtils.isEmpty( dataMap ) ) {
                String state = dataMap.getOrDefault( "state", "" ).toString();

                //  4代付中 5代付失败 6代付成功
                int status      = 4;
                int orderStatus = 0;
                switch ( state ) {
                case "3":
                    status = 6;
                    orderStatus = 1;
                    break;
                case "4":
                case "5":
                case "6":
                case "7":
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

