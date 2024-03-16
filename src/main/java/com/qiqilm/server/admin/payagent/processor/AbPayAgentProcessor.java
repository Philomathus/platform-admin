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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.AB_PAY + "PayAgentProcessor" )
@Log4j2
public class AbPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        Map<String, Object> params = new TreeMap<>();
        params.put( "appKey", payAgentPlatform.getMerId() );
        params.put( "nonce", UuidUtil.getRandomUuidWithoutSeparator() );
        params.put( "busTransType", "2" );
        params.put( "busRecordId", withdrawLog.getOrderNo() );
        params.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        params.put( "userPayAddress", withdrawLog.getBankAccount() );
        params.put( "busCreateTime", withdrawLog.getCreateTime().getTime() );
        params.put( "timestamp", System.currentTimeMillis() );
        params.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl4( params ) + signMd5;

        log.warn( tempStr );

        params.put( "signature", DigestUtils.md5Hex( tempStr ) );
        log.warn( payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json( params ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( params ),
                reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "code", "" ).toString();
            if ( "200".equals( code ) ) {
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
        String              merchantOrderNo = requestMap.getOrDefault( "busRecordId", "" ).toString();
        Map<String, Object> dataMap         = ( Map<String, Object> ) requestMap.getOrDefault( "data", new HashMap<>() );
        String              status          = dataMap.getOrDefault( "status", "" ).toString();
        String              sign            = dataMap.remove( "signature" ).toString();

        //去除空值参数
        dataMap.values().removeIf( value -> value == null || StringUtils.isBlank( value.toString() ) );
        Map<String, Object> requestMapTree = new TreeMap<>( dataMap );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl4( requestMapTree ) + signMd5;
        String rspSign = DigestUtils.md5Hex( tempStr );

        log.info( payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign );
        if ( rspSign.equalsIgnoreCase( sign ) ) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( merchantOrderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", merchantOrderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", merchantOrderNo );
                return "success";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", merchantOrderNo );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( merchantOrderNo );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals( status ) );
            log.info( payAgentPlatform.getName()
                    + "订单号:{},回调状态:{},", merchantOrderNo, "2".equals( status ) ? "成功" : "失败" );
            return "success";
        }
        log.warn( payAgentPlatform.getName() + "验签失败" );
        return "fail";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                     String realIp ) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put( "status", false );
        resultMap.put( "msg", null );
        if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
            log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
            resultMap.put( "msg", "请求ip非白名单:" + realIp );
            return resultMap;
        }
        log.warn( "反查数据:" + JsonUtil.object2Json( requestMap ) );
        String     orderNo = requestMap.get( "orderNo" ).toString();
        BigDecimal amount  = new BigDecimal( requestMap.getOrDefault( "amount", 0 ).toString() );
        String     appKey  = resultMap.get( "appKey" ).toString();
        String     sign    = resultMap.remove( "sign" ).toString();

        SortedMap<String, Object> signMap = new TreeMap<>( requestMap );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String signRsp = DigestUtils.md5Hex( this.assemblyUrl4( signMap ) + signMd5 );

        if ( StringUtils.equalsIgnoreCase( signRsp, sign ) ) {
            MemberWithdrawLog memberWithdrawLog = withdrawLogMapper.selectByOrderNo( orderNo );
            if ( memberWithdrawLog == null || amount.compareTo( memberWithdrawLog.getWithdrawMoney() ) != 0
                    || !appKey.equals( payAgentPlatform.getMerId() ) ) {
                resultMap.put( "msg", "订单不匹配" );
                return resultMap;
            }
            resultMap.put( "status", true );
            resultMap.put( "msg", "验证成功" );
            return resultMap;
        }
        resultMap.put( "msg", "验签失败" );
        return resultMap;
    }

    @Override
    public String queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
        MemberWithdrawLog withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform  payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );

        Map<String, Object> params = new TreeMap<>();
        params.put( "appKey", payAgentPlatform.getMerId() );
        params.put( "busRecordId", withdrawLog.getOrderNo() );
        params.put( "busTransType", "4" );
        params.put( "type", "2" );
        params.put( "nonce", UuidUtil.getRandomUuidWithoutSeparator() );
        params.put( "timestamp", System.currentTimeMillis() );

        String tempStr = this.assemblyUrl4( params ) + payAgentPlatform.getSignPublicKey();
        params.put( "signature", DigestUtils.md5Hex( tempStr ) );

        log.warn( payAgentPlatform.getName() + "查询请求参数{}", JsonUtil.object2Json( params ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( params ), null );

        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "code", "" ).toString();
            if ( "200".equals( code ) ) {
                Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
                //  status
                //  0-处理中 , 1-成功，2-失败
                int statusCode = Integer.parseInt( dataMap.getOrDefault( "status", -1 ).toString() );

                //  4代付中 5代付失败 6代付成功
                int status;
                int orderStatus;
                switch ( statusCode ) {
                case 2:
                    status = 6;
                    orderStatus = 1;
                    break;
                case 1:
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
