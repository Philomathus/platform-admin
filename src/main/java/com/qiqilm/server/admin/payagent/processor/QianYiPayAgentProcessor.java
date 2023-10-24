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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.QIANYI_PAY + "PayAgentProcessor" )
@Log4j2
public class QianYiPayAgentProcessor extends AbstractPayAgent {


    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "merchantUUID", payAgentPlatform.getMerId() );
        dataMap.put( "merchantOrderNo", withdrawLog.getOrderNo() );
        dataMap.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).toString() );
        dataMap.put( "channelCode", "666" );
        dataMap.put( "productType", "chn.bank.payout" );
        dataMap.put( "currency", "CNY" );
        dataMap.put( "approveType", "2" );
        dataMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        dataMap.put( "sign", DigestUtils.md5Hex( signStr ).toUpperCase() );
        Map<String, Object> extInfoMap = new HashMap<>();
        extInfoMap.put( "bankName", withdrawLog.getBankName() );
        extInfoMap.put( "fullName", withdrawLog.getBankUserName() );
        extInfoMap.put( "bankcardNumber", withdrawLog.getBankAccount() );
        dataMap.put( "extInfo",  extInfoMap );

        log.warn( payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json( dataMap ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( dataMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String              code          = resultMap.getOrDefault( "code", "" ).toString();
            Map<String, Object> resultDataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
            if ( "0".equals( code ) && !CollectionUtils.isEmpty( resultDataMap ) && "3".equals( resultDataMap
                    .getOrDefault( "status", "0" )
                    .toString() ) ) {
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

        String merchantOrderNo = requestMap.getOrDefault( "merchantOrderNo", "" ).toString();
        String sysOrderNo      = requestMap.getOrDefault( "sysOrderNo", "" ).toString();
        String status          = requestMap.getOrDefault( "status", "" ).toString();
        String sign            = requestMap.remove( "sign" ).toString();

        Map<String, Object> treeMap = new TreeMap<>( requestMap );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String signStr = DigestUtils.md5Hex( this.assemblyUrl( treeMap ) + "&key=" + signMd5 );

        if ( signStr.equalsIgnoreCase( sign ) ) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( merchantOrderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", merchantOrderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", merchantOrderNo );
                return "SUCCESS";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", merchantOrderNo );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( merchantOrderNo );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, sysOrderNo, payAgentPlatform, "4".equals( status ) );
            log.info( payAgentPlatform.getName()
                    + "订单号:{},回调状态:{},", merchantOrderNo, "4".equals( status ) ? "成功" : "失败" );
            return "SUCCESS";
        }
        log.warn( payAgentPlatform.getName() + "验签失败" );
        return "FAIL";
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
        dataMap.put( "merchantUUID", payAgentPlatform.getMerId() );
        dataMap.put( "merchantOrderNo", withdrawLog.getOrderNo() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        dataMap.put( "sign", DigestUtils.md5Hex( this.assemblyUrl( dataMap ) + "&key=" + signMd5 ).toUpperCase() );

        log.warn( payAgentPlatform.getName() + "查询请求参数{}", JsonUtil.object2Json( dataMap ) );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( dataMap, httpHeaders );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderQueryAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return JsonUtil.json2Map( text );
            } );
            log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                //  statusCode
                //  1-成功，2-失败，3-处理中，4-订单不存在 5-审核拒绝
                String statusCode = resultMap.getOrDefault( "status", "" ).toString();

                if ( "3".equals( statusCode ) || "4".equals( statusCode ) || "5".equals( statusCode ) ) {
                    //  4代付中 5代付失败 6代付成功
                    int status      = 4;
                    int orderStatus = 0;
                    if ( "4".equals( statusCode ) ) {
                        status      = 6;
                        orderStatus = 1;
                    } else if ( "5".equals( statusCode ) ) {
                        status      = 5;
                        orderStatus = 2;
                    }
                    payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                            orderStatus );
                }
                return resultMap.getOrDefault( "msg", "" ).toString();
            }

        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}


