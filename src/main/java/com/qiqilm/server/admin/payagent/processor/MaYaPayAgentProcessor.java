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
import org.springframework.util.ObjectUtils;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.MAYA_PAY + "PayAgentProcessor" )
@Log4j2
public class MaYaPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "Timestamp", System.currentTimeMillis() / 1000 );
        bodyMap.put( "AccessKey", payAgentPlatform.getMerId() );
        bodyMap.put( "PayChannelId", "600" );
        bodyMap.put( "Payee", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "PayeeNo", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "PayeeAddress", withdrawLog.getBankAddress() );
        bodyMap.put( "OrderNo", withdrawLog.getOrderNo() );
        bodyMap.put( "Amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "CallbackUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = this.assemblyUrl( bodyMap ) + "&SecretKey=" + signMd5;

        String sign = DigestUtils.md5Hex( signStr ).toLowerCase();
        bodyMap.put( "Sign", sign );

        log.info( "请求参数:" + JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "0".equals( resultMap.getOrDefault( "Code", "" ).toString() ) ) {
                log.info( "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "Message", "" ).toString() );
                // 回滚订单
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( "代付订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "fail";
        }

        String orderNo    = requestMap.getOrDefault( "OrderNo", "" ).toString();
        String signTmp    = requestMap.remove( "Sign" ).toString();
        int    orderState = Integer.parseInt( requestMap.getOrDefault( "Status", -1 ).toString() );

        // 解密后对签名验证

        SortedMap<String, Object> signMap = new TreeMap<>( requestMap );
        if ( ObjectUtils.isEmpty( signMap.get( "Ext" ) ) ) {
            signMap.remove( "Ext" );
        }
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = this.assemblyUrl( signMap ) + "&SecretKey=" + signMd5;
        log.info( signStr );
        String sign = DigestUtils.md5Hex( signStr ).toLowerCase();

        log.info( payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signTmp );
        if ( sign.equalsIgnoreCase( signTmp ) ) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - OrderNo:{}", orderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - OrderNo:{}", orderNo );
                return "ok";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - OrderNo:{}", orderNo );
                return "ok";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderNo );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, orderNo, payAgentPlatform, orderState == 4 );
            return "ok";
        }
        log.error( payAgentPlatform.getName() + "回调验签失败" );
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
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "Timestamp", System.currentTimeMillis() / 1000 );
        bodyMap.put( "AccessKey", payAgentPlatform.getMerId() );
        bodyMap.put( "OrderNo", withdrawLog.getOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        // 生成签名信息
        String signStr = this.assemblyUrl( bodyMap ) + "&SecretKey=" + signMd5;
        String sign    = DigestUtils.md5Hex( signStr ).toLowerCase();
        bodyMap.put( "Sign", sign );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( bodyMap ), null );

        log.warn( JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "Code", "" ).toString();
            if ( "0".equals( code ) ) {
                Map<String, Object> resultDataMap = ( Map<String, Object> ) resultMap.getOrDefault( "Data", new HashMap<>() );
                int                 orderStatus   = Integer.parseInt( resultDataMap.getOrDefault( "Status", 0 ).toString() );

                // status 4代付中5代付失败6代付成功
                // orderState (0=处理中，1=成功，2=失败)

                int status     = 4;
                int orderState = 0;
                switch ( orderStatus ) {
                case 4:
                    status = 6;
                    orderState = 1;
                    break;
                case 16:
                    status = 5;
                    orderState = 2;
                    break;
                default:
                    break;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState );
                return "查询成功";
            } else if ( "-1".equals( code ) ) { // 订单不存在
                // 回滚订单
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
                return "订单不存在, 已回滚";
            }
        }
        log.warn( "代付订单查询失败 - result:{}", JsonUtil.object2Json( resultMap ) );
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
