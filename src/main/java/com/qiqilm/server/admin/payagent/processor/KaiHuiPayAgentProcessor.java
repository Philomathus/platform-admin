package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.KAI_HUI_PAY + "PayAgentProcessor" )
@Log4j2
public class KaiHuiPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "member_code", payAgentPlatform.getMerId() );

        Map<String, String> dataMap = new TreeMap<>();
        dataMap.put( "order_amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).toString() );
        dataMap.put( "order_id", withdrawLog.getOrderNo() );
        dataMap.put( "bank_name", withdrawLog.getBankName().trim() );
        dataMap.put( "card_address", StringUtils.isBlank( withdrawLog.getBankAddress() ) ? "" : withdrawLog.getBankAddress() );
        dataMap.put( "card_nummber", withdrawLog.getBankAccount().trim() );
        dataMap.put( "card_name", withdrawLog.getBankUserName().trim() );
        dataMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        dataMap.put( "callback_url", "" );

        bodyMap.put( "data", JsonUtil.object2Json( dataMap ) );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = this.assemblyUrl3( dataMap ) + signMd5;
        log.warn( signStr );
        bodyMap.put( "sign", DigestUtils.md5Hex( signStr ) );

        log.warn( JsonUtil.object2Json( bodyMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName() + "代付下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "code", "" ).toString();
            if ( "20000".equals( code ) ) {
                log.info( payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "errmsg", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "代付订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String                    sign    = requestMap.remove( "sign" ).toString();
        String                    state   = requestMap.getOrDefault( "status", "" ).toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String signStr = DigestUtils.md5Hex( this.assemblyUrl3( bodyMap ) + signMd5 );

        log.info( payAgentPlatform.getName() + "代付回调签名字符串:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            String orderId = ( String ) requestMap.get( "merchant_order_no" );

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
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "1".equals( state ) );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", orderId, "1".equals( state ) ? "成功" : "失败" );
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
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put( "member_code", payAgentPlatform.getMerId() );
        paramsMap.put( "order_no", withdrawLog.getOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String sign = DigestUtils.md5Hex( this.assemblyUrl3( paramsMap ) + signMd5 );
        paramsMap.put( "sign", sign );


        try {
            Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageForm( paramsMap )
                    , null );

            log.info( payAgentPlatform.getName() + "代付查询结果- result:{}", JsonUtil.object2Json( resultMap ) );
            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                String              code    = resultMap.getOrDefault( "code", "" ).toString();
                Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
                if ( "20000".equals( code ) ) {
                    int enStatus = Integer.parseInt( dataMap.getOrDefault( "en_status", 0 ).toString() );
                    // status 4代付中5代付失败6代付成功
                    // orderState 1-待付款 2-付款成功 3-付款失败

                    int status     = 4;
                    int orderState = 0;
                    switch ( enStatus ) {
                    case 1:
                        status = 6;
                        orderState = 1;
                        break;
                    case 2:
                        break;
                    default:
                        status = 5;
                        orderState = 2;
                        break;
                    }

                    log.warn( "orderState:{}", enStatus );

                    payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                            orderState );
                    return dataMap.getOrDefault( "notify_status", "" ).toString();
                }
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
