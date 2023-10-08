package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeHuaZhongType;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
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
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Repository ( value = ConstantsPayAgent.HUA_ZHONG2_PAY + "PayAgentProcessor" )
@Log4j2
public class HuaZhong2PayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {

        BankCodeHuaZhongType bankCodeType = BankCodeHuaZhongType.getCodeByDesc( withdrawLog.getBankName() );
        if ( bankCodeType == null ) {
            bankCodeType = BankCodeHuaZhongType.QTBC;
        }
        withdrawLog.setBankCode( bankCodeType.name() );

        Map<String, Object> params = new TreeMap<>();
        params.put( "merchantId", payAgentPlatform.getMerId() );
        params.put( "version", "1.0.0" );
        params.put( "merchantOrderNo", withdrawLog.getOrderNo() );
        params.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        params.put( "bankCode", withdrawLog.getBankCode() );
        params.put( "bankcardAccountNo", withdrawLog.getBankAccount() );
        params.put( "bankcardAccountName", withdrawLog.getBankUserName() );
        params.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String tempStr = this.assemblyUrl( params );
        String sign    = RSACoder.signSha1Rsa( tempStr, payAgentPlatform.getSignPrivateKey() );
        params.put( "sign", sign );
        log.warn( payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json( params ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageJson( params ), reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "code", "" ).toString();
            if ( "0".equals( code ) ) {
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
        String status          = requestMap.getOrDefault( "status", "" ).toString();
        String sign            = requestMap.remove( "sign" ).toString();

        Map<String, Object> requestMapTree = new TreeMap<>( requestMap );
        String              tempStr        = this.assemblyUrl( requestMapTree );

        if ( RSACoder.verifySha1Rsa( tempStr, payAgentPlatform.getSignPublicKey(), sign ) ) {
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
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "1".equals( status ) );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", merchantOrderNo, "1".equals( status ) ? "成功" : "失败" );
            return "success";
        }
        log.warn( payAgentPlatform.getName() + "验签失败" );
        return "fail";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        return null;
    }

    @Override
    public String queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
        MemberWithdrawLog withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform  payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );

        Map<String, Object> params = new TreeMap<>();
        params.put( "merchantId", payAgentPlatform.getMerId() );
        params.put( "version", "1.0.0" );
        params.put( "merchantOrderNo", withdrawLog.getOrderNo() );
        params.put( "submitTime", DateFormatUtils.formate( new Date(), DateFormatUtils.TIGHT_PATTERN_DATETIME ) );

        String tempStr = this.assemblyUrl( params );
        String sign    = RSACoder.signSha1Rsa( tempStr, payAgentPlatform.getSignPrivateKey() );
        params.put( "sign", sign );

        log.warn( payAgentPlatform.getName() + "查询请求参数{}", JsonUtil.object2Json( params ) );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( params, httpHeaders );

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
                String code = resultMap.getOrDefault( "code", "" ).toString();
                if ( "0".equals( code ) ) {
                    //  status
                    //  0-处理中 , 1-成功，2-失败
                    String statusCode = resultMap.getOrDefault( "status", "" ).toString();

                    if ( "0".equals( statusCode ) || "1".equals( statusCode ) || "2".equals( statusCode ) ) {
                        //  4代付中 5代付失败 6代付成功
                        int status      = 4;
                        int orderStatus = 0;
                        if ( "1".equals( statusCode ) ) {
                            status = 6;
                            orderStatus = 1;
                        } else {
                            status = 5;
                            orderStatus = 2;
                        }
                        payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderStatus );
                    }
                }
                return resultMap.getOrDefault( "msg", "" ).toString();
            }

        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
