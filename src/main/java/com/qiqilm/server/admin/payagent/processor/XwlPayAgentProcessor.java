package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;

@Repository( value = ConstantsPayAgent.XWL + "PayAgentProcessor" )
@Log4j2
public class XwlPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        Map<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "mchId", payAgentPlatform.getMerId() );
        bodyMap.put( "mchOrderNo", withdrawLog.getOrderNo() );
        bodyMap.put( "version", "1.0" );
        bodyMap.put( "requestTime", DateFormatUtils.formate( new Date(), DateFormatUtils.TIGHT_PATTERN_DATETIME ) );
        bodyMap.put( "withdrawAmt", withdrawLog.getWithdrawMoney().setScale( 0, RoundingMode.HALF_UP ).toString() );
        bodyMap.put( "bankUserName", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "bankAccountNo", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "bankName", withdrawLog.getBankName().trim() );
        bodyMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String tempStr = this.assemblyUrl( bodyMap );

        log.warn( tempStr );

        //RSA2证书为2048位，使用算法SHA256withRSA。
        String sign = RSACoder.signSha256Rsa( tempStr, payAgentPlatform.getSignPrivateKey() );
        bodyMap.put( "sign", sign );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );

        log.warn( payAgentPlatform.getName() + "下单结果:" + JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "200".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
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
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "fail";
        }
        String sign = requestMap.remove( "sign" ).toString();

        SortedMap<String, Object> signMap  = new TreeMap<>( requestMap );
        String                    signData = this.assemblyUrl( signMap );

        if ( RSACoder.verifySha256Rsa( signData, payAgentPlatform.getSignPublicKey(), sign ) ) {
            String            state       = signMap.getOrDefault( "status", "" ).toString();
            String            orderNo     = signMap.getOrDefault( "mchOrderNo", "" ).toString();
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", orderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", orderNo );
                return "success";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", orderNo );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderNo );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, orderNo, payAgentPlatform, "3".equals( state ) );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", orderNo, "3".equals( state ) ? "成功" : "失败" );
            return "success";
        }
        log.info( payAgentPlatform.getName() + "回调验签失败" );
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

        Map<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "mchOrderNo", withdrawLog.getOrderNo() );
        bodyMap.put( "mchId", payAgentPlatform.getMerId() );
        bodyMap.put( "version", "1.0" );
        bodyMap.put( "requestTime", DateFormatUtils.formate( new Date(), DateFormatUtils.TIGHT_PATTERN_DATETIME ) );

        String tempStr = this.assemblyUrl( bodyMap );
        bodyMap.put( "sign", RSACoder.signSha256Rsa( tempStr, payAgentPlatform.getSignPrivateKey() ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( bodyMap ), null );

        log.warn( payAgentPlatform.getName() + "查询结果:" + JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "200".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );

                int state = Integer.parseInt( dataMap.getOrDefault( "status", -1 ).toString() );

                // status 4代付中 5代付失败 6代付成功
                // state 1处理中 2支付成功 3支付失败
                int status;
                int orderStatus;
                switch ( state ) {
                case 3:
                    status = 6;
                    orderStatus = 1;
                    break;
                case 1:
                case 2:
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
            return resultMap.getOrDefault( "msg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
