package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeGTType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;

@Repository( value = ConstantsPayAgent.GT_PAY + "PayAgentProcessor" )
@Log4j2
@SuppressWarnings( "unchecked" )
public class GtPayPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        BankCodeGTType bankCodeType = BankCodeGTType.getCodeByDesc( withdrawLog.getBankName() );
        if ( bankCodeType == null ) {
            payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            log.warn( "此代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName() );
            throw new BusinessException( "此代付无法支持的银行类型：" + withdrawLog.getBankName() );
        }
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "merchantNum", payAgentPlatform.getMerId() );
        bodyMap.put( "merchantOrderNum", withdrawLog.getOrderNo() );
        bodyMap.put( "userCardNum", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "bankCode", bankCodeType.name() );
        bodyMap.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );
        bodyMap.put( "sign", sign );

        bodyMap.put( "callbackUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put( "userCardName", withdrawLog.getBankUserName().trim() );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String success = resultMap.getOrDefault( "success", "" ).toString();
            if ( "true".equals( success ) ) {
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "message", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
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

        String rspSign = requestMap.remove( "sign" ).toString();

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put( "bankCode", requestMap.get( "bankCode" ) );
        dataMap.put( "merchantNum", requestMap.get( "merchantNum" ) );
        dataMap.put( "status", requestMap.get( "status" ) );
        dataMap.put( "sysOrderNum", requestMap.get( "sysOrderNum" ) );
        dataMap.put( "userCardNum", requestMap.get( "userCardNum" ) );

        String signMd5     = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr     = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        String signRebuilt = DigestUtils.md5Hex( tempStr );

        log.warn( "Callback: {}, {}, {} ", tempStr, signRebuilt, rspSign );

        if ( rspSign.equalsIgnoreCase( signRebuilt ) ) {
            String  merOrder = requestMap.getOrDefault( "merchantOrderNum", "" ).toString();
            String  tradeNo  = requestMap.getOrDefault( "sysOrderNum", "" ).toString();
            Integer status   = ( Integer ) requestMap.getOrDefault( "status", "" );
            if ( StringUtils.isBlank( merOrder ) ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", merOrder );
                return "FAIL";
            }
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( merOrder );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", merOrder );
                return "FAIL";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", merOrder );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( merOrder );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, tradeNo, payAgentPlatform, 6 == status );
            return "SUCCESS";
        }

        return "FAIL";
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

        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put( "merchantNum", payAgentPlatform.getMerId() );
        paramsMap.put( "merchantOrderNum", withdrawLog.getOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( paramsMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );
        paramsMap.put( "sign", sign );

        log.warn( JsonUtil.object2Json( paramsMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( paramsMap ),
                null );
        if ( !CollectionUtils.isEmpty( resultMap ) && "true".equals( resultMap.getOrDefault( "success", "" ).toString() ) ) {
            Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                Integer status     = ( Integer ) dataMap.getOrDefault( "status", "" );
                int     orderState = 6 == status ? 1 : 2;
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState );
            }
            return resultMap.getOrDefault( "message", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
