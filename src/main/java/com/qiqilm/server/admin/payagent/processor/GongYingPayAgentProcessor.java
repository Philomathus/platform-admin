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

@Repository( value = ConstantsPayAgent.GONG_YING_PAY + "PayAgentProcessor" )
@Log4j2
public class GongYingPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "mchid", payAgentPlatform.getMerId() );
        bodyMap.put( "out_trade_no", withdrawLog.getOrderNo() );
        bodyMap.put( "money", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "bankname", withdrawLog.getBankName().trim() );
        bodyMap.put( "subbranch", withdrawLog.getBankName().trim() );
        bodyMap.put( "accountname", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "cardnumber", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "notifyurl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        bodyMap.put( "sign", DigestUtils.md5Hex( tempStr ).toUpperCase() );
        bodyMap.put( "cardtype", "0" );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String status = resultMap.getOrDefault( "status", "" ).toString();
            if ( "success".equals( status ) ) {
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
        String signRes = requestMap.remove( "sign" ).toString();
        requestMap.remove( "success_time" );

        Map<String, Object> dataMap = new TreeMap<>( requestMap );
        String              signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );
        if ( signRes.equalsIgnoreCase( sign ) ) {
            String            withdrawOrderId = dataMap.getOrDefault( "out_trade_no", "" ).toString();
            String            refCode         = dataMap.getOrDefault( "code", "" ).toString();
            MemberWithdrawLog withdrawLog     = withdrawLogMapper.selectByOrderNo( withdrawOrderId );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", withdrawOrderId );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 0 ) {
                log.error( "已有代付记录 - merOrderNo:{}", withdrawOrderId );
                return "OK";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( withdrawOrderId );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, requestMap.getOrDefault( "transaction_id", "" )
                    .toString(), payAgentPlatform, "3".equals( refCode ) );
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
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "out_trade_no", withdrawLog.getOrderNo() );
        dataMap.put( "mchid", payAgentPlatform.getMerId() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        dataMap.put( "sign", DigestUtils.md5Hex( tempStr ).toUpperCase() );

        log.warn( JsonUtil.object2Json( dataMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageForm( dataMap ), null );

        log.info( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String success = resultMap.getOrDefault( "status", "" ).toString();
            int    refCode = Integer.parseInt( resultMap.getOrDefault( "refCode", "-1" ).toString() );
            if ( "success".equals( success ) ) {
                // status 4代付中 5代付失败 6代付成功
                // refCode 1成功 2失败 3处理中 4待处理
                int status;
                switch ( refCode ) {
                case 3:
                    status = 6;
                    break;
                case 4:
                case 5:
                    status = 5;
                    break;
                default:
                    status = 4;
                    break;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, refCode );
            }
            return resultMap.getOrDefault( "msg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
