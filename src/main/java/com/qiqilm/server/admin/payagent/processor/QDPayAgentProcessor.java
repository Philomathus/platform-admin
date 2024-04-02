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
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.QD_PAY + "PayAgentProcessor" )
@Log4j2
public class QDPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawDetail, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "merchantId", payAgentPlatform.getMerId() );
        bodyMap.put( "orderNo", withdrawDetail.getOrderNo() );
        bodyMap.put( "amount", withdrawDetail.getWithdrawMoney().setScale( 0, RoundingMode.HALF_UP ) );
        bodyMap.put( "walletAddress", withdrawDetail.getBankAccount().trim() );
        bodyMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        String signStr = this.assemblyUrl( bodyMap ) + "&key="
                + RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        bodyMap.put( "sign", DigestUtils.md5Hex( signStr ).toUpperCase() );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawDetail.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "200".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
                if ( !CollectionUtils.isEmpty( dataMap ) ) {
                    log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                    int status = Integer.parseInt( dataMap.getOrDefault( "status", "0" ).toString() );
                    return status == 1 || status == 2;
                }
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "msg", "" ).toString() );
                payAgentService.callBackOrder( withdrawDetail, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawDetail.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "fail";
        }
        String sign      = requestMap.remove( "sign" ).toString();
        String orderNo = requestMap.getOrDefault( "orderNo", "" ).toString();
        String status    = requestMap.getOrDefault( "status", "" ).toString();

        MemberWithdrawLog withdrawDetail = withdrawLogMapper.selectByOrderNo( orderNo );
        if ( withdrawDetail == null ) {
            log.error( "提现相关记录丢失 - merOrderNo:{}", orderNo );
            return "fail";
        }
        if ( withdrawDetail.getStatus() == 6 ) {
            log.error( "已有代付记录 - merOrderNo:{}", orderNo );
            return "success";
        }
        PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderNo );

        // 去除空值
        requestMap.entrySet().removeIf( me -> me.getValue() == null || StringUtils.isBlank( me.getValue().toString() ) );

        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        String signStr = this.assemblyUrl( bodyMap ) + "&key="
                + RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String mySign = DigestUtils.md5Hex( signStr ).toUpperCase();
        if ( mySign.equalsIgnoreCase( sign ) ) {
            boolean isSuccess = "1".equals( status );
            payAgentService.processOrderPay( withdrawDetail, payAgentLog, orderNo, payAgentPlatform, isSuccess );
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
        MemberWithdrawLog withdrawDetail   = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform  payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );

        Map<String, Object> params = new TreeMap<>();
        params.put( "merchantId", payAgentPlatform.getMerId() );
        params.put( "orderNo", withdrawDetail.getOrderNo() );

        String tempStr = this.assemblyUrl( params ) + "&key="
                + RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        params.put( "sign", DigestUtils.md5Hex( tempStr ).toUpperCase() );

        log.info( payAgentPlatform.getName()
                + "查询请求参数 - orderNo:{},request:{}", withdrawDetail.getOrderNo(), JsonUtil.object2Json( params ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( params ), null );

        log.info( payAgentPlatform.getName()
                + "查询结果 - orderNo:{},result:{}", withdrawDetail.getOrderNo(), JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) && "200".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
            Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
            if ( !CollectionUtils.isEmpty( dataMap ) ) {
                int orderState = Integer.parseInt( dataMap.getOrDefault( "status", 0 ).toString() );
                // status 4代付中5代付失败6代付成功
                // orderState (0处理失败，1处理成功，2处理中)
                int status;
                int orderStatus;
                switch ( orderState ) {
                case 1:
                    status = 6;
                    orderStatus = 1;
                    break;
                case 0:
                    status = 5;
                    orderStatus = 2;
                    break;
                default:
                    status = 4;
                    orderStatus = 0;
                    break;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawDetail, withdrawDetail.getUpdateTime(), status,
                        orderStatus );
                return resultMap.getOrDefault( "msg", "" ).toString();
            }
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawDetail.getOrderNo();
    }
}
