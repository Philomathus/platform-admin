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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.XIANGYUE + "PayAgentProcessor" )
@Log4j2
public class XiangYuePayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, String> bodyMap = new TreeMap<>();
        bodyMap.put( "name", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "Card", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "Bankof", withdrawLog.getBankName().trim() );
        bodyMap.put( "money", withdrawLog.getWithdrawMoney().setScale( 0, RoundingMode.HALF_UP ).toString() );
        bodyMap.put( "remarks", withdrawLog.getOrderNo() );
        bodyMap.put( "mchid", payAgentPlatform.getMerId() );
        bodyMap.put( "notifyurl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        //MD5(name + Card + Bankof + money + remarks + mchid + notifyurl + 商户key + MD5(支付密码))"
        //headerKey配上各商户的支付密码(支付密码就是登陆密码)
        String MD5Password = DigestUtils.md5Hex( payAgentPlatform.getHeaderKey() );
        String tempStr = bodyMap.get( "name" ) + bodyMap.get( "Card" ) + bodyMap.get( "Bankof" ) + bodyMap.get( "money" )
                + bodyMap.get( "remarks" ) + bodyMap.get( "mchid" ) + bodyMap.get( "notifyurl" ) + signMd5
                + MD5Password.toLowerCase();
        String sign = DigestUtils.md5Hex( tempStr );
        bodyMap.put( "sign", sign );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( bodyMap, httpHeaders );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return JsonUtil.json2Map( text );
            } );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
            reqPayAgent.setFailReason( payAgentPlatform.getName() + "下单报错原因:" + e );
        }
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String result = resultMap.getOrDefault( "result", "" ).toString();
            if ( "true".equals( result ) ) {
                log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "ims", "" ).toString() );

                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String sign = requestMap.get( "sign" ).toString();
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        //2=成功，4=驳回
        String state = requestMap.get( "state" ).toString();
        //MD5(单号 + 订单状态 + 商户ID + 备注信息 + 商户key)
        String tempStr =
                requestMap.get( "odd" ).toString() + state + requestMap.get( "mchid" ).toString() + requestMap.get( "remarks" )
                                                                                                              .toString()
                        + signMd5;
        String signStr = DigestUtils.md5Hex( tempStr );

        log.info( payAgentPlatform.getName() + "回调签名字符串:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            String remarks = ( String ) requestMap.get( "remarks" );

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( remarks );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", remarks );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", remarks );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( remarks );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals( state ) );
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
        MemberWithdrawLog   withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform    payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
        Map<String, String> paramsMap        = new TreeMap<>();
        paramsMap.put( "odd", withdrawLog.getOrderNo() );
        paramsMap.put( "mchid", payAgentPlatform.getMerId() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        //md5(订单号 + 商户ID + 商户key)
        String tempStr = paramsMap.get( "odd" ) + paramsMap.get( "mchid" ) + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );
        paramsMap.put( "sign", sign );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( paramsMap, httpHeaders );

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
            log.info( payAgentPlatform.getName() + "查询结果- result:{}", JsonUtil.object2Json( resultMap ) );
            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                String result = resultMap.getOrDefault( "result", "" ).toString();
                if ( "true".equals( result ) ) {
                    int zt = Integer.parseInt( resultMap.getOrDefault( "zt", "" ).toString() );
                    if ( zt > 1 ) {
                        // status 4代付中 5代付失败 6代付成功
                        // zt（0=待处理、1=处理中、2=成功、3=失败、4=驳回）
                        int status = 4;
                        if ( zt == 2 ) {
                            status = 6;
                        } else if ( zt == 3 || zt == 4 ) {
                            status = 5;
                        }
                        payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, zt );
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
