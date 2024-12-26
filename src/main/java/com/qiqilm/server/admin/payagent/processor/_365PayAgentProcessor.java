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
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository( value = ConstantsPayAgent._365_PAY + "PayAgentProcessor" )
@Log4j2
public class _365PayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> params = new TreeMap<>();
        params.put( "userCode", payAgentPlatform.getMerId() );
        params.put( "timestamp", System.currentTimeMillis() );
        params.put( "orderCode", withdrawLog.getOrderNo() );
        params.put( "amount", withdrawLog.getWithdrawMoney().setScale( 0, RoundingMode.HALF_UP ).toString() );
        params.put( "address", withdrawLog.getBankAccount() );
        params.put( "realNameInfo", withdrawLog.getBankUserName() );
        params.put( "callbackUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        // MD5(userCode&timestamp&orderCode&amount&address&realNameInfo&key)
        String tempStr = String.format( "%s&%s&%s&%s&%s&%s&%s", params.get( "userCode" ), params.get( "timestamp" ),
                params.get( "orderCode" ), params.get( "amount" ), params.get( "address" ), params.get( "realNameInfo" ),
                signMd5 );
        params.put( "sign", DigestUtils.md5Hex( tempStr ).toUpperCase() );

        log.warn( JsonUtil.object2Json( params ) );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( packageJson( params ) ), response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return JsonUtil.json2Map( text );
            } );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
            Pattern pattern = Pattern.compile( "\\{.*\\}" );
            Matcher matcher = pattern.matcher( e.getMessage() );
            if ( matcher.find() ) {
                String jsonContent = matcher.group();
                resultMap = JsonUtil.json2Map( jsonContent );
            }
        }

        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "200".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
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
        String rspSign = requestMap.remove( "sign" ).toString();

        String signMd5           = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String status            = requestMap.getOrDefault( "status", "1" ).toString();
        String orderCode         = requestMap.getOrDefault( "orderCode", "" ).toString();
        String platformOrderCode = requestMap.getOrDefault( "platformOrderCode", "" ).toString();
        String amount            = requestMap.getOrDefault( "amount", "-1" ).toString();
        String userCode          = requestMap.getOrDefault( "userCode", "" ).toString();
        String withdrawTime      = requestMap.getOrDefault( "withdrawTime", "" ).toString();

        // MD5(userCode&orderCode&platformOrderCode&amount&status&withdrawTime&key)
        String signStr =
                userCode + "&" + orderCode + "&" + platformOrderCode + "&" + amount + "&" + status + "&" + withdrawTime + "&"
                        + signMd5;
        String mySign = DigestUtils.md5Hex( signStr ).toUpperCase();

        log.info( payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + mySign );
        if ( rspSign.equalsIgnoreCase( mySign ) ) {

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderCode );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", orderCode );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 3 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", orderCode );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "已有代付记录 - merOrderNo:{}", orderCode );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderCode );
            boolean     isSuccess   = "2".equals( status );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, orderCode, payAgentPlatform, isSuccess );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", orderCode, isSuccess ? "成功" : "失败" );
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
        paramsMap.put( "userCode", payAgentPlatform.getMerId() );
        paramsMap.put( "orderCode", withdrawLog.getOrderNo() );
        paramsMap.put( "timestamp", System.currentTimeMillis() );
        paramsMap.put( "customerOrderCode", "" );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        // MD5(userCode&timestamp&orderCode&key)
        String signStr =
                paramsMap.get( "userCode" ) + "&" + paramsMap.get( "timestamp" ) + "&" + paramsMap.get( "orderCode" ) + "&"
                        + signMd5;
        paramsMap.put( "sign", DigestUtils.md5Hex( signStr ).toUpperCase() );

        log.warn( JsonUtil.object2Json( paramsMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageJson( paramsMap ),
                null );
        if ( !CollectionUtils.isEmpty( resultMap ) && "200".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
            Map<String, Object> payParams = ( Map<String, Object> ) resultMap.getOrDefault( "data", Collections.emptyMap() );
            int                 status    = Integer.parseInt( payParams.getOrDefault( "status", "1" ).toString() );
            int                 orderStatus;
            int                 orderState;
            switch ( status ) {
            case 2:
                orderStatus = 6;
                orderState = 1;
                break;
            case 3:
                orderStatus = 5;
                orderState = 2;
                break;
            default:
                orderStatus = 0;
                orderState = 0;
                break;
            }
            payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), orderStatus, orderState );
            return resultMap.getOrDefault( "message", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
