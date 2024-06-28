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
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.SHUN_FENG + "PayAgentProcessor" )
@Log4j2
public class ShunFengPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        Map<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "mch_id", payAgentPlatform.getMerId() );
        bodyMap.put( "out_trade_no", withdrawLog.getOrderNo() );
        bodyMap.put( "money", withdrawLog.getWithdrawMoney().setScale( 2, BigDecimal.ROUND_HALF_UP ) );
        bodyMap.put( "pay_type", "1" );
        bodyMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put( "bank_name", withdrawLog.getBankName().trim() );
        bodyMap.put( "acct_name", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "acct_no", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "open_name", withdrawLog.getBankAddress().trim() );
        bodyMap.put( "timestamp", DateFormatUtils.formate( reqPayAgent.getCurrentTime(),
                DateFormatUtils.SPLIT_PATTERN_DATETIME ) );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = this.assemblyUrl( bodyMap ) + signMd5;
        String sign    = DigestUtils.md5Hex( signStr );
        bodyMap.put( "sign", sign );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "0".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                Map    dataMap = ( Map ) resultMap.getOrDefault( "data", "" );
                String status  = dataMap.getOrDefault( "state", "" ).toString();
                if ( "1".equals( status ) ) {
                    log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                    return true;
                }
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
            return "fail";
        }
        String                    signRes = requestMap.remove( "sign" ).toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );
        String                    signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String                    tempStr = this.assemblyUrl( bodyMap ) + signMd5;

        String sign         = DigestUtils.md5Hex( tempStr ).toUpperCase();
        String out_trade_no = requestMap.getOrDefault( "out_trade_no", "" ).toString();
        String state        = requestMap.getOrDefault( "state", "" ).toString();

        log.info( payAgentPlatform.getName() + "回调验签:" + signRes + "_" + sign );
        if ( signRes.equals( sign ) ) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( out_trade_no );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", out_trade_no );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", out_trade_no );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( out_trade_no );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, out_trade_no, payAgentPlatform, "3".equals( state ) );
            return "SUCCESS";
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
        MemberWithdrawLog   withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform    payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
        Map<String, Object> bodyMap          = new TreeMap<>();
        bodyMap.put( "mch_id", payAgentPlatform.getMerId() );
        bodyMap.put( "out_trade_no", withdrawLog.getOrderNo() );
        bodyMap.put( "timestamp", DateFormatUtils.formate( payAgentLog.getCreateTime(),
                DateFormatUtils.SPLIT_PATTERN_DATETIME ) );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String signStr = this.assemblyUrl( bodyMap ) + signMd5;
        String sign    = DigestUtils.md5Hex( signStr );
        bodyMap.put( "sign", sign );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( bodyMap ), null );

        log.info( payAgentPlatform.getName() + "查询结果- result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "0".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
                int                 state   = Integer.parseInt( dataMap.getOrDefault( "state", -1 ).toString() );
                // status 4代付中 5代付失败 6代付成功
                // state 1处理中 2支付成功 3支付失败
                int status = 4;
                if ( state == 2 ) {
                    status = 6;
                } else if ( state == 3 ) {
                    status = 5;
                }
                log.warn( "state:{}", state );
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, state );
            }
            return resultMap.getOrDefault( "msg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
