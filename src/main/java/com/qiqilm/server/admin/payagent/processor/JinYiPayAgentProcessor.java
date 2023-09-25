package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AESCoder;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.JIN_YI_PAY + "PayAgentProcessor" )
@Log4j2
public class JinYiPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {

        TreeMap<String, String> param = new TreeMap<>();
        param.put( "mno", payAgentPlatform.getMerId() );
        param.put( "orderno", withdrawLog.getOrderNo() );
        param.put( "amount", String.valueOf( withdrawLog.getWithdrawMoney().multiply( BigDecimal.valueOf( 100 ) )
                                                        .setScale( 0, RoundingMode.HALF_UP ) ) );
        param.put( "pt_id", "4" );
        param.put( "bankname", withdrawLog.getBankName() );
        param.put( "bankaddress", withdrawLog.getBankAddress() );
        param.put( "name", withdrawLog.getBankUserName() );
        param.put( "account", withdrawLog.getBankAccount() );
        param.put( "async_notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( param ) + "&key=" + signMd5;

        String sign = DigestUtils.md5Hex( tempStr ).toLowerCase();
        param.put( "sign", sign );

        String message = JsonUtil.object2Json( sign );
        log.warn( message );

        Map<String, Object> params = new HashMap<>();
        params.put( "mno", payAgentPlatform.getMerId() );
        params.put( "content", AESCoder.encryptByKey( message, payAgentPlatform.getSignPublicKey() ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( params ),
                reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "code", "" ).toString();
            if ( "success".equals( code ) ) {
                Map<String, Object> dataMap = ( Map<String, Object> ) resultMap.get( "data" );
                if ( !CollectionUtils.isEmpty( dataMap ) ) {
                    String defrayStatus = dataMap.getOrDefault( "status", "" ).toString();
                    if ( "0".equals( defrayStatus ) ) {
                        log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                        return true;
                    } else {
                        reqPayAgent.setFailReason( resultMap.getOrDefault( "msg", "" ).toString() );
                    }
                }
            }
        }
        log.info( payAgentPlatform.getName() + "订单提交失败 - 订单号:{}", withdrawLog.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String content = requestMap.remove( "content" ).toString();
        String orderno = requestMap.getOrDefault( "orderno", "" ).toString();

        String jsonData = AESCoder.decryptByKey( content, payAgentPlatform.getSignPublicKey() );

        SortedMap<String, Object> data = new TreeMap<>( JsonUtil.json2Map( jsonData ) );

        String sign = data.remove( "sign" ).toString();

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = DigestUtils.md5Hex( this.assemblyUrl( data ) + "&key=" + signMd5 );

        log.info( payAgentPlatform.getName() + "回调签名:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            String status = data.get( "status" ).toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderno );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", orderno );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", orderno );
                return "success";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", orderno );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderno );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "1".equals( status ) );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", orderno, "1".equals( status ) ? "成功" : "失败" );
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
        MemberWithdrawLog withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform  payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );

        TreeMap<String, String> param = new TreeMap<>();
        param.put( "mno", payAgentPlatform.getMerId() );
        param.put( "orderno", withdrawLog.getOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( param ) + "&key=" + signMd5;

        String sign = DigestUtils.md5Hex( tempStr ).toLowerCase();
        param.put( "sign", sign );

        String message = JsonUtil.object2Json( sign );
        log.warn( message );

        Map<String, Object> params = new HashMap<>();
        params.put( "mno", payAgentPlatform.getMerId() );
        params.put( "content", AESCoder.encryptByKey( message, payAgentPlatform.getSignPublicKey() ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( params ), null );

        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "code", 0 ).toString();
            if ( "success".equals( code ) ) {
                Map<String, Object> dataMap    = ( Map<String, Object> ) resultMap.get( "data" );
                int                 statusType = Integer.parseInt( dataMap.getOrDefault( "status", 0 ).toString() );
                // status 4代付中 5代付失败 6代付成功
                // statusType 0：待处理， 1：处理中， 2：已打款， 3：已拒绝 ， 4：已退单
                int status = 4;
                if ( statusType == 1 ) {
                    status = 6;
                } else if ( statusType == 2 ) {
                    status = 5;
                }
                payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType );
                return resultMap.getOrDefault( "msg", "" ).toString();
            }
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
