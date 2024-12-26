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

@Repository( value = ConstantsPayAgent.QIYUE_PAY + "PayAgentProcessor" )
@Log4j2
public class QiYuePayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "app_id", payAgentPlatform.getMerId() );
        bodyMap.put( "product_id", "888" );
        bodyMap.put( "out_trade_no", withdrawLog.getOrderNo() );
        bodyMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).toString() );
        bodyMap.put( "time", System.currentTimeMillis() / 1000 );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        log.warn( tempStr );
        bodyMap.put( "sign", DigestUtils.md5Hex( tempStr ) );

        Map<String, Object> extMap = CollectionUtils.newHashMap( 3 );
        extMap.put( "accountName", withdrawLog.getBankUserName() );
        extMap.put( "accountNumber", withdrawLog.getBankAccount() );
        extMap.put( "bankName", withdrawLog.getBankName() );
        bodyMap.put( "ext", extMap );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            int code = Integer.parseInt( resultMap.getOrDefault( "code", "-1" ).toString() );
            if ( code == 200 ) {
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
        }
        String                    sign    = requestMap.remove( "sign" ).toString();
        String                    state   = requestMap.getOrDefault( "trade_status", "" ).toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex( tempStr );

        log.info( payAgentPlatform.getName() + "回调签名字符串:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            String shOrderId = ( String ) requestMap.get( "out_trade_no" );

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( shOrderId );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", shOrderId );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", shOrderId );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( shOrderId );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, ( String ) requestMap.get( "trade_no" ),
                    payAgentPlatform, "1".equals( state ) );
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

        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put( "app_id", payAgentPlatform.getMerId() );
        paramsMap.put( "out_trade_no", withdrawLog.getOrderNo() );
        paramsMap.put( "time", System.currentTimeMillis() / 1000 );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( paramsMap ) + "&key=" + signMd5;
        paramsMap.put( "sign", DigestUtils.md5Hex( tempStr ) );

        log.warn( JsonUtil.object2Json( paramsMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( paramsMap ),
                null );

        log.info( payAgentPlatform.getName()
                + "查询结果 - 订单号:{} - result:{}", payAgentLog.getWithdrawOrderNo(), JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            int                 code    = Integer.parseInt( resultMap.getOrDefault( "code", "-1" ).toString() );
            Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
            if ( 200 == code && !CollectionUtils.isEmpty( dataMap ) ) {
                int status = Integer.parseInt( dataMap.getOrDefault( "trade_status", "-1" ).toString() );
                if ( status == -1 ) {
                    return "订单正在代付中,请稍后";
                }
                int orderState    = 1 == status ? 1 : 2;
                int return_status = status == 1 ? 6 : 5;
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), return_status,
                        orderState );
            }
            return resultMap.getOrDefault( "message", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
