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

import java.math.RoundingMode;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.BAISHENG_PAY + "PayAgentProcessor" )
@Log4j2
public class BaiShengPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();

        bodyMap.put( "appId", payAgentPlatform.getMerId() );
        bodyMap.put( "appOrderNo", withdrawLog.getOrderNo() );
        bodyMap.put( "orderAmt", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "payId", "401" );
        bodyMap.put( "accNo", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "accName", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "bankName", withdrawLog.getBankName() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;

        log.warn( tempStr );

        String sign = DigestUtils.md5Hex( tempStr ).toUpperCase();
        bodyMap.put( "sign", sign );

        bodyMap.put( "notifyURL", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果- result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "code", "" ).toString();
            if ( "0000".equals( code ) ) {
                log.info( payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "msg", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String sign = requestMap.remove( "sign" ).toString();

        String withdrawOrderId = requestMap.getOrDefault( "appOrderNo", "" ).toString();
        String orderNo         = requestMap.getOrDefault( "orderNo", "" ).toString();
        String status          = requestMap.getOrDefault( "orderStatus", "" ).toString();

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex( tempStr ).toUpperCase();

        log.info( payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( withdrawOrderId );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", withdrawOrderId );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 1 ) {
                log.error( "已有代付记录 - merOrderNo:{}", withdrawOrderId );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( withdrawOrderId );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, orderNo, payAgentPlatform, "02".equals( status ) );
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
        dataMap.put( "appId", payAgentPlatform.getMerId() );
        dataMap.put( "appOrderNo", payAgentLog.getWithdrawOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr ).toUpperCase();
        dataMap.put( "sign", sign );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageForm( dataMap ), null );

        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "0000".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {

                String orderState = resultMap.getOrDefault( "orderStatus", 0 ).toString();

                int status      = 4;
                int orderStatus = 0;
                if ( "00".equals( orderState ) || "01".equals( orderState ) || "02".equals( orderState )
                        || "03".equals( orderState ) || "99".equals( orderState ) ) {
                    if ( "02".equals( orderState ) ) {
                        status      = 6;
                        orderStatus = 1;
                    } else if ( "99".equals( orderState ) ) {
                        status      = 5;
                        orderStatus = 2;
                    }
                }

                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderStatus );
            }
            return resultMap.getOrDefault( "msg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
