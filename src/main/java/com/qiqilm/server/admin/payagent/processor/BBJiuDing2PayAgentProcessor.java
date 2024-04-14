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
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.BB_JIUDING2_PAY + "PayAgentProcessor" )
@Log4j2
public class BBJiuDing2PayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawDetail, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "merchant_no", payAgentPlatform.getMerId() );
        bodyMap.put( "out_trade_no", withdrawDetail.getOrderNo() );
        bodyMap.put( "amount", withdrawDetail.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).toString() );
        bodyMap.put( "bank_name", withdrawDetail.getBankName() );
        bodyMap.put( "account_name", withdrawDetail.getBankUserName().trim() );
        bodyMap.put( "account", withdrawDetail.getBankAccount().trim() );
        bodyMap.put( "payment_type", "prepaid" );
        bodyMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        log.warn( tempStr );
        bodyMap.put( "sign", DigestUtils.md5Hex( tempStr ) );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawDetail.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "0".equals( resultMap.getOrDefault( "code", "" ).toString() ) && resultMap.get( "data" ) != null ) {
                log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
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

        String signRes         = requestMap.remove( "sign" ).toString();
        String withdrawOrderId = requestMap.getOrDefault( "out_trade_no", "" ).toString();

        MemberWithdrawLog withdrawDetail = withdrawLogMapper.selectByOrderNo( withdrawOrderId );
        if ( withdrawDetail == null ) {
            log.error( "提现相关记录丢失 - merOrderNo:{}", withdrawOrderId );
            return "fail";
        }
        PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( withdrawOrderId );

        Map<String, Object> dataMap = new TreeMap<>( requestMap );
        String              signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );
        if ( signRes.equalsIgnoreCase( sign ) ) {
            if ( withdrawDetail.getStatus() == 0 ) {
                log.error( "已有代付记录 - merOrderNo:{}", withdrawOrderId );
                return "success";
            }

            String  status  = dataMap.getOrDefault( "status", "" ).toString();
            boolean success = "success".equals( status );
            payAgentService.processOrderPay( withdrawDetail, payAgentLog, requestMap
                    .getOrDefault( "order_no", "" )
                    .toString(), payAgentPlatform, success );

            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", withdrawOrderId, success ? "成功" : "失败" );
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

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "out_trade_no", withdrawDetail.getOrderNo() );
        dataMap.put( "merchant_no", payAgentPlatform.getMerId() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        dataMap.put( "sign", DigestUtils.md5Hex( tempStr ) );

        log.warn( JsonUtil.object2Json( dataMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( dataMap ), null );

        log.info( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String              code       = resultMap.getOrDefault( "code", "" ).toString();
            Map<String, Object> dataResMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
            if ( "0".equals( code ) && !CollectionUtils.isEmpty( dataResMap ) ) {
                String statusRes = dataResMap.getOrDefault( "status", "" ).toString();
                // status 4代付中 5代付失败 6代付成功
                int status;
                switch ( statusRes ) {
                case "SUCCESSFUL":
                    status = 6;
                    break;
                case "FAILURE":
                    status = 5;
                    break;
                default:
                    status = 4;
                    break;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawDetail, withdrawDetail.getUpdateTime(), status, 0 );
            }
            return resultMap.getOrDefault( "msg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawDetail.getOrderNo();
    }
}
