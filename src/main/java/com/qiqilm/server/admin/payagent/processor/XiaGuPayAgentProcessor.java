package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeXiaGuType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository ( value = ConstantsPayAgent.XIA_GU_PAY + "PayAgentProcessor" )
@Log4j2
public class XiaGuPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        BankCodeXiaGuType bankCodeType = BankCodeXiaGuType.getCodeByDesc( withdrawLog.getBankName() );
        if ( bankCodeType == null ) {
            log.warn( payAgentPlatform.getName() + "代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName() );
            payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            throw new BusinessException( "此代付无法支持的银行类型：" + withdrawLog.getBankName() );
        }
        withdrawLog.setBankCode( bankCodeType.name() );

        SortedMap<String, Object> bodyMap = new TreeMap<>();

        bodyMap.put( "mchid", payAgentPlatform.getMerId() );
        bodyMap.put( "out_trade_no", withdrawLog.getOrderNo() );
        bodyMap.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ) );
        bodyMap.put( "bank_code", withdrawLog.getBankCode() );
        bodyMap.put( "notify_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put( "bank_number", withdrawLog.getBankAccount().trim() );
        bodyMap.put( "bank_owner", withdrawLog.getBankUserName().trim() );
        bodyMap.put( "body", "123" );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( bodyMap ) + "&" + signMd5;

        String sign = DigestUtils.md5Hex( tempStr );
        bodyMap.put( "sign", sign );

        Map<String, Object> resultMap = this.sendPostMap( payAgentPlatform.getPayOrderAddr(), packageForm( bodyMap ),
                reqPayAgent );

        log.info( payAgentPlatform.getName() + "下单结果- result:{}", JsonUtil.object2Json( resultMap ) );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            if ( "1".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                log.info( payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
                reqPayAgent.setFailReason( resultMap.getOrDefault( "msg", "" ).toString() );
            }
        }
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String sign = requestMap.remove( "sign" ).toString();

        String withdrawOrderId = requestMap.getOrDefault( "out_trade_no", "" ).toString();
        String status          = requestMap.getOrDefault( "status", "" ).toString();

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        String tempStr = this.assemblyUrl( bodyMap ) + "&" + signMd5;
        String signStr = DigestUtils.md5Hex( tempStr );

        log.info( payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( withdrawOrderId );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", withdrawOrderId );
                return "FAIL";
            }
            if ( withdrawLog.getStatus() == 0 ) {
                log.error( "已有代付记录 - merOrderNo:{}", withdrawOrderId );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( withdrawOrderId );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals( status ) );
            return "SUCCESS";
        }
        return "FAIL";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        return null;
    }

    @Override
    public String queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
        MemberWithdrawLog withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform  payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "mchid", payAgentPlatform.getMerId() );
        dataMap.put( "out_trade_no", payAgentLog.getWithdrawOrderNo() );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( dataMap ) + "&" + signMd5;

        String sign = DigestUtils.md5Hex( tempStr );
        dataMap.put( "sign", sign );

        Map<String, Object> resultMap = null;
        try {
            resultMap = this.sendPostMap( payAgentPlatform.getPayOrderQueryAddr(), packageForm( dataMap ), null );
            log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                Map<String, Object> resultDataMap = ( Map<String, Object> ) resultMap.getOrDefault( "data", new HashMap<>() );
                if ( "1".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
                    int                 orderStatus   = Integer.parseInt( resultDataMap.getOrDefault( "status", 0 ).toString() );

                    // status 4代付中5代付失败6代付成功
                    // orderState (0=处理中，1=成功，2=失败)

                    int status     = 4;
                    int orderState = 0;
                    switch ( orderStatus ) {
                        case 2:
                            status = 6;
                            orderState = 1;
                            break;
                        case 0:
                            status = 5;
                            orderState = 2;
                            break;
                        default:
                            break;
                    }

                    payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                            orderState );
                    return "查询成功";
                }

            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return payAgentPlatform.getName() + "代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
