package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import com.qiqilm.server.admin.utils.UuidUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.SUIYIFU_PAY + "PayAgentProcessor" )
@Log4j2
public class SuiYiFuPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        Map<String, Object> params = new TreeMap<>();
        params.put( "merId", payAgentPlatform.getMerId() );
        params.put( "orderId", withdrawLog.getOrderNo() );
        params.put( "money", withdrawLog.getWithdrawMoney().setScale( 0, RoundingMode.HALF_UP ) );
        params.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        params.put( "nonceStr", UuidUtil.getRandomUuidWithoutSeparator() );
        params.put( "name", withdrawLog.getBankUserName() );
        params.put( "ka", withdrawLog.getBankAccount() );
        params.put( "bank", withdrawLog.getBankName() );
        params.put( "zhihang", "广东省广州市" );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( params ) + "&key=" + signMd5;

        log.warn( tempStr );

        String sign = DigestUtils.md5Hex( tempStr ).toUpperCase();
        params.put( "sign", sign );

        log.warn( payAgentPlatform.getName() + "下单请求参数:{}", JsonUtil.object2Json( params ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( params ),
                reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "1".equals( resultMap.getOrDefault( "code", "" ).toString() ) && resultMap.containsKey( "data" ) ) {
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
        String              rspSign = requestMap.remove( "sign" ).toString();
        String              signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        Map<String, Object> treeMap = new TreeMap<>( requestMap );
        String              tempStr = this.assemblyUrl( treeMap ) + "&key=" + signMd5;
        String              sign    = DigestUtils.md5Hex( tempStr ).toUpperCase();

        log.info( payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign );
        if ( rspSign.equalsIgnoreCase( sign ) ) {
            String orderId = requestMap.getOrDefault( "orderId", "" ).toString();
            String status  = requestMap.getOrDefault( "status", "" ).toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderId );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", orderId );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", orderId );
                return "success";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", orderId );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderId );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals( status ) );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", orderId, "2".equals( status ) ? "成功" : "失败" );
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

        Map<String, Object> params = new TreeMap<>();
        params.put( "merId", payAgentPlatform.getMerId() );
        params.put( "orderId", withdrawLog.getOrderNo() );
        params.put( "nonceStr", UuidUtil.getRandomUuidWithoutSeparator() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( params ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr ).toUpperCase();
        params.put( "sign", sign );

        log.warn( payAgentPlatform.getName() + "下单请求参数:{}", JsonUtil.object2Json( params ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( params ), null );

        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "code", "" ).toString();
            if ( "1".equals( code ) ) {
                //  status
                //  0-处理中 , 1-成功，2-失败
                int statusCode = Integer.parseInt( resultMap.getOrDefault( "status", "-1" ).toString() );

                if ( statusCode >= 0 && statusCode <= 4 ) {
                    //  4代付中 5代付失败 6代付成功
                    int status;
                    int orderStatus;
                    switch ( statusCode ) {
                    case 2:
                        status = 6;
                        orderStatus = 1;
                        break;
                    case 3:
                    case 4:
                        status = 5;
                        orderStatus = 2;
                        break;
                    default:
                        status = 4;
                        orderStatus = 0;
                        break;
                    }
                    payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                            orderStatus );
                }
            }
            return resultMap.getOrDefault( "msg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
