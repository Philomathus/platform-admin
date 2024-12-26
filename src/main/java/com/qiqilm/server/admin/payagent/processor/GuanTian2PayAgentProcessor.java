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

@Repository( value = ConstantsPayAgent.GUAN_TIAN_2_PAY + "PayAgentProcessor" )
@Log4j2
public class GuanTian2PayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "user", payAgentPlatform.getMerId() );
        bodyMap.put( "user_order_no", withdrawLog.getOrderNo() );
        bodyMap.put( "account_name", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "account_number", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "bank_name", withdrawLog.getBankName() );
        bodyMap.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).toString() );
        bodyMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key="
                + RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        log.warn( tempStr );
        bodyMap.put( "sign", DigestUtils.md5Hex( tempStr ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果- result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "1".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                log.info( payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "message", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String sign = requestMap.remove( "sign" ).toString();

        String withdrawOrderId = requestMap.getOrDefault( "user_order_no", "" ).toString();
        String status          = requestMap.getOrDefault( "status", "" ).toString();
        String orderNo         = requestMap.getOrDefault( "order_no", "" ).toString();

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex( tempStr );

        log.info( payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( withdrawOrderId );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", withdrawOrderId );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 1 ) {
                log.error( "已有代付记录 - merOrderNo:{}", withdrawOrderId );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( withdrawOrderId );
            boolean     successFlag = "success".equals( status );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, successFlag );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", orderNo, successFlag ? "成功" : "失败" );
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

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "user", payAgentPlatform.getMerId() );
        dataMap.put( "user_order_no", payAgentLog.getWithdrawOrderNo() );

        String tempStr = this.assemblyUrl( dataMap ) + "&key="
                + RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        dataMap.put( "sign", DigestUtils.md5Hex( tempStr ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( dataMap ), null );

        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "1".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {

                String orderStatus = resultMap.getOrDefault( "status", "" ).toString();

                // status 4代付中5代付失败6代付成功
                // orderState (0=处理中，1=成功，2=失败)

                int status     = 4;
                int orderState = 0;
                switch ( orderStatus ) {
                case "fail":
                    status = 5;
                    orderState = 2;
                    break;
                case "success":
                    status = 6;
                    orderState = 1;
                    break;
                default:
                    break;
                }

                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState );
                return resultMap.getOrDefault( "message", "" ).toString();
            }
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
