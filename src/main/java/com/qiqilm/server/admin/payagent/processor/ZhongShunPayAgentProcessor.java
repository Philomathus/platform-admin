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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.ZHONG_SHUN_PAY + "PayAgentProcessor" )
@Log4j2
public class ZhongShunPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "payment_amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "payment_orderid", withdrawLog.getOrderNo() );
        bodyMap.put( "payment_bankcode", "666" );
        bodyMap.put( "payment_bankname", withdrawLog.getBankName().trim() );
        if ( StringUtils.isNotBlank( withdrawLog.getBankAddress() ) ) {
            bodyMap.put( "payment_subbranch", withdrawLog.getBankAddress().trim() );
        } else {
            bodyMap.put( "payment_subbranch", "广东省广州市" );
        }
        bodyMap.put( "payment_cardnumber", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "payment_accountname", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "payment_province", "广东省" );
        bodyMap.put( "payment_city", "广州市" );
        bodyMap.put( "payment_memberid", payAgentPlatform.getMerId() );
        bodyMap.put( "payment_notifyurl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;

        log.warn( tempStr );

        bodyMap.put( "payment_md5sign", DigestUtils.md5Hex( tempStr ).toUpperCase() );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "success".equals( resultMap.getOrDefault( "status", "" ).toString() )
                    && resultMap.containsKey( "transaction_id" ) ) {
                log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "msg", "" ).toString() );

                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String sign           = requestMap.remove( "payment_md5sign" ).toString();
        String refCode        = requestMap.getOrDefault( "refCode", "" ).toString();
        String transaction_id = requestMap.getOrDefault( "transaction_id", "" ).toString();

        //去除空值参数
        requestMap.values().removeIf( value -> value == null || StringUtils.isBlank( value.toString() ) );

        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex( tempStr ).toUpperCase();

        log.info( payAgentPlatform.getName() + "回调签名字符串:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            String out_trade_id = requestMap.getOrDefault( "out_trade_no", "" ).toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( out_trade_id );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", out_trade_id );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", out_trade_id );
                return "OK";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( out_trade_id );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, transaction_id, payAgentPlatform, "1".equals( refCode ) );
            return "OK";
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
        MemberWithdrawLog   withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform    payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
        Map<String, Object> paramsMap        = new TreeMap<>();
        paramsMap.put( "payment_memberid", payAgentPlatform.getMerId() );
        paramsMap.put( "payment_orderid", withdrawLog.getOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( paramsMap ) + "&key=" + signMd5;
        paramsMap.put( "payment_md5sign", DigestUtils.md5Hex( tempStr ).toUpperCase() );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageForm( paramsMap ),
                null );
        log.info( payAgentPlatform.getName() + "查询结果:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            int refCode = Integer.parseInt( resultMap.getOrDefault( "refCode", "-1" ).toString() );
            int status  = 4;
            switch ( refCode ) {
            case 1:
                status = 6;
                break;
            case 2:
            case 5:
                status = 5;
                break;
            }
            payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, 0 );
            return resultMap.getOrDefault( "msg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
