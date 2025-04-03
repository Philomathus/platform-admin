package com.qiqilm.server.admin.payagent.processor;

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
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.FU_GUI + "PayAgentProcessor" )
@Log4j2
public class FuGuiPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "cash_amount", withdrawLog.getWithdrawMoney().stripTrailingZeros().toPlainString() );
        bodyMap.put( "app_id", payAgentPlatform.getMerId() );
        // bodyMap.put( "card_type", "1" );
        bodyMap.put( "bank_name", withdrawLog.getBankName().trim() );
        bodyMap.put( "bank_account", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "payee_name", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "time", System.currentTimeMillis() / 1000 );
        bodyMap.put( "cash_no", withdrawLog.getOrderNo() );
        bodyMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        log.warn( tempStr );
        bodyMap.put( "sign", DigestUtils.md5Hex( DigestUtils.md5Hex( tempStr ).toUpperCase() ) );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "0".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", Collections.emptyMap() );
                if ( "waiting".equals( dataMap.getOrDefault( "status", "" ).toString() ) ) {
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
        String sign   = requestMap.remove( "sign" ).toString();
        String status = requestMap.getOrDefault( "status", "-1" ).toString();

        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String rspSign = DigestUtils.md5Hex( DigestUtils.md5Hex( tempStr ).toUpperCase() );

        log.info( payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign );
        if ( sign.equalsIgnoreCase( rspSign ) ) {
            String mchOrderNo  = requestMap.getOrDefault( "cash_no", "-1" ).toString();
            String platOrderNo = requestMap.getOrDefault( "local_no", "-1" ).toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( mchOrderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", mchOrderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", mchOrderNo );
                return "success";
            }
            boolean     isSuccess   = "finish".equals( status );
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( mchOrderNo );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, platOrderNo, payAgentPlatform, isSuccess );

            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", mchOrderNo, isSuccess ? "成功" : "失败" );
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

        Map<String, String> paramsMap = new TreeMap<>();
        paramsMap.put( "app_id", payAgentPlatform.getMerId() );
        paramsMap.put( "cash_no", withdrawLog.getOrderNo() );
        paramsMap.put( "time", String.valueOf( System.currentTimeMillis() / 1000 ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( paramsMap ) + "&key=" + signMd5;
        paramsMap.put( "sign", DigestUtils.md5Hex( DigestUtils.md5Hex( tempStr ).toUpperCase() ) );

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.setAll( paramsMap );

        UriComponents uriComponents = UriComponentsBuilder.fromUriString( payAgentPlatform.getPayOrderQueryAddr() )
                .queryParams( multiValueMap ).build();

        Map<String, Object> resultMap = this.sendGetMap( uriComponents.toUriString(), null );

        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String retCode = resultMap.getOrDefault( "code", "" ).toString();
            if ( "0".equals( retCode ) ) {
                Map<String, Object> dataMap    = ( Map<String, Object> ) resultMap.getOrDefault( "data", Collections.emptyMap() );
                String              statusType = dataMap.getOrDefault( "status", "-1" ).toString();
                // waiting=待处理，back=驳回，finish=成功完结, reverse=冲正
                int status;
                int orderStatus;
                switch ( statusType ) {
                case "finish":
                    status = 6;
                    orderStatus = 1;
                    break;
                case "waiting":
                    status = 4;
                    orderStatus = 0;
                    break;
                default:
                    status = 5;
                    orderStatus = 2;
                    break;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderStatus );
            }
            return resultMap.getOrDefault( "message", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
