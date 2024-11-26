package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.HJDF_PAY + "PayAgentProcessor" )
@Log4j2
public class HjdfPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "merchantNum", payAgentPlatform.getMerId() );
        dataMap.put( "orderNo", withdrawLog.getOrderNo() );
        dataMap.put( "amount", withdrawLog.getWithdrawMoney().stripTrailingZeros().toPlainString() );
        dataMap.put( "cardNumber", withdrawLog.getBankAccount().trim() );
        dataMap.put( "payType", "DF001" );
        dataMap.put( "account", withdrawLog.getBankUserName() );
        dataMap.put( "bankName", withdrawLog.getBankName() );
        dataMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = payAgentPlatform.getMerId() + withdrawLog.getOrderNo() + dataMap.get( "amount" ) + signMd5;
        dataMap.put( "sign", DigestUtils.md5Hex( tempStr ) );

        log.warn( payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json( dataMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( dataMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "200".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
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
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            return "fail";
        }

        String orderNo    = requestMap.getOrDefault( "orderNo", "" ).toString();
        String signTmp    = requestMap.remove( "sign" ).toString();
        int    orderState = Integer.parseInt( requestMap.getOrDefault( "state", -1 ).toString() );

        // 解密后对签名验证

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = String.valueOf( requestMap.get( "state" ) ) + requestMap.get( "merchantNum" ) + requestMap.get( "orderNo" )
                        + requestMap.get( "amount" ) + signMd5;
        log.info( signStr );
        String sign = DigestUtils.md5Hex( signStr );

        log.info( payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signTmp );
        if ( sign.equalsIgnoreCase( signTmp ) ) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - OrderNo:{}", orderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - OrderNo:{}", orderNo );
                return "success";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - OrderNo:{}", orderNo );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderNo );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, orderNo, payAgentPlatform, orderState == 1 );
            return "success";
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
        MemberWithdrawLog withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform  payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "merchantNum", payAgentPlatform.getMerId() );
        dataMap.put( "orderNo", withdrawLog.getOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = payAgentPlatform.getMerId() + withdrawLog.getOrderNo() + signMd5;
        dataMap.put( "sign", DigestUtils.md5Hex( tempStr ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageForm( dataMap ), null );

        log.info( payAgentPlatform.getName()
                + "查询结果 - 订单号:{} - result:{}", payAgentLog.getWithdrawOrderNo(), JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "code", "" ).toString();
            if ( !"200".equals( code ) ) {
                return resultMap.getOrDefault( "msg", "" ).toString();
            }

            Map<String, Object> resultDataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
            int                 orderStatus   = Integer.parseInt( resultDataMap.getOrDefault( "state", 0 ).toString() );

            // status 4代付中5代付失败6代付成功
            // 1待处理 2处理中 3已完成 4出款失败 5驳回待处理（不代表失败）

            int status     = 4;
            int orderState = 0;
            switch ( orderStatus ) {
            case 3:
                status = 6;
                orderState = 1;
                break;
            case 4:
                status = 5;
                orderState = 2;
                break;
            default:
                break;
            }
            payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState );
            return resultMap.getOrDefault( "msg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
