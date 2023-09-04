package com.qiqilm.server.admin.payagent.processor;

import com.fasterxml.jackson.core.type.TypeReference;
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

import java.math.RoundingMode;
import java.util.*;

@Repository( value = ConstantsPayAgent.ZIXING_PAY + "PayAgentProcessor" )
@Log4j2
public class ZiXingPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {

        Map<String, Object> params = new LinkedHashMap<>();
        params.put( "fxid", payAgentPlatform.getMerId() );
        params.put( "fxaction", "repay" );

        Map<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "fxddh", withdrawLog.getOrderNo() );
        bodyMap.put( "fxdate", DateFormatUtils.formate( new Date(), "yyyyMMddHHmmss" ) );
        bodyMap.put( "fxfee", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "fxbody", withdrawLog.getBankAccount() );
        bodyMap.put( "fxname", withdrawLog.getBankUserName() );
        bodyMap.put( "fxzhihang", withdrawLog.getBankAddress() );

        params.put( "fxbody", JsonUtil.object2Json( Collections.singleton( bodyMap ) ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl2( params ) + signMd5;
        log.warn( tempStr );
        params.put( "fxsign", DigestUtils.md5Hex( tempStr ) );
        params.put( "fxnotifyurl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        log.warn( payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json( params ) );


        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( params ),
                reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "fxstatus", "" ).toString();

            if ( "1".equals( code ) ) {
                List<Map<String, Object>> fxbodyListMap = JsonUtil.json2Array( resultMap.getOrDefault( "fxbody", "" )
                                                                                        .toString(),
                        new TypeReference<List<Map<String, Object>>>() {} );
                int fxstatus = Integer.parseInt( fxbodyListMap.get( 0 ).getOrDefault( "fxstatus", "0" ).toString() );
                if ( fxstatus > 0 ) {
                    log.info( payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json( resultMap ) );
                    return true;
                } else {
                    log.error( payAgentPlatform.getName() + "订单提交失败 - listResult:{}", JsonUtil.object2Json( resultMap ) );
                }
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "fxmsg", "" ).toString() );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String merchantOrderNo = requestMap.getOrDefault( "fxddh", "" ).toString();
        String status          = requestMap.getOrDefault( "fxstatus", "" ).toString();
        String signRes         = requestMap.remove( "fxsign" ).toString();

        Map<String, Object> requestMapTree = new LinkedHashMap<>();
        requestMapTree.put( "fxstatus", status );
        requestMapTree.put( "fxid", requestMap.get( "fxid" ) );
        requestMapTree.put( "fxddh", merchantOrderNo );
        requestMapTree.put( "fxfee", requestMap.get( "fxfee" ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl2( requestMapTree ) + signMd5;
        String sign    = DigestUtils.md5Hex( tempStr );
        if ( signRes.equalsIgnoreCase( sign ) ) {
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
            log.info( payAgentPlatform.getName()
                    + "订单号:{},回调状态:{},", merchantOrderNo, "1".equals( status ) ? "成功" : "失败" );
            return "success";
        }
        log.warn( payAgentPlatform.getName() + "验签失败" );
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

        Map<String, Object> params = new LinkedHashMap<>();
        params.put( "fxid", payAgentPlatform.getMerId() );
        params.put( "fxaction", "repayquery" );

        Map<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "fxddh", withdrawLog.getOrderNo() );

        params.put( "fxbody", Collections.singleton( bodyMap ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String tempStr = this.assemblyUrl2( params ) + signMd5;
        params.put( "fxsign", DigestUtils.md5Hex( tempStr ) );

        log.warn( payAgentPlatform.getName() + "查询请求参数{}", JsonUtil.object2Json( params ) );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageForm( params ), null );
        log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String code = resultMap.getOrDefault( "fxcode", "" ).toString();
            if ( "1".equals( code ) ) {
                Map<String, Object> fxbodyMap = ( Map<String, Object> ) resultMap.getOrDefault( "fxbody", new HashMap<>() );
                int                 fxstatus  = Integer.parseInt( fxbodyMap.getOrDefault( "fxstatus", "0" ).toString() );

                if ( fxstatus >= 0 ) {
                    //  4代付中 5代付失败 6代付成功
                    int status      = 4;
                    int orderStatus = 0;

                    switch ( fxstatus ) {
                    case 1:
                        status = 6;
                        orderStatus = 1;
                        break;
                    case 2:
                    case 3:
                        status = 5;
                        orderStatus = 2;
                        break;
                    }
                    payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                            orderStatus );
                }
            }
            return resultMap.getOrDefault( "fxmsg", "" ).toString();
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
