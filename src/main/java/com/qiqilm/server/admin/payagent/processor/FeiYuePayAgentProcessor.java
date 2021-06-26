package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeFeiYueType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.FEI_YUE + "PayAgentProcessor" )
@Log4j2
public class FeiYuePayAgentProcessor extends AbstractPayAgent {
	@Override
	public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		BankCodeFeiYueType bankCodeType = BankCodeFeiYueType.getCodeByDesc( withdrawLog.getBankName() );
		if ( bankCodeType == null ) {
			payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
			log.warn( "此代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName() );
			throw new BusinessException( "此代付无法支持的银行类型：" + withdrawLog.getBankName() );
		}
		withdrawLog.setBankCode( bankCodeType.name().substring( 1 ) );

		SortedMap<String, String> bodyMap = new TreeMap<>();
		bodyMap.put( "userId", payAgentPlatform.getMerId() );
		bodyMap.put( "outTradeNo", withdrawLog.getOrderNo() );
		bodyMap.put( "subbranchName", withdrawLog.getBankAddress() );
		bodyMap.put( "bankCode", withdrawLog.getBankCode() );
		bodyMap.put( "bankCardNo", withdrawLog.getBankAccount().trim() );
		bodyMap.put( "bankAccount", withdrawLog.getBankUserName().trim() );
		bodyMap.put( "subbranchProvince", "中国" );
		bodyMap.put( "orderScore", withdrawLog.getWithdrawMoney().setScale( 0, RoundingMode.HALF_UP ).toString() );
		bodyMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + ConstantsPayAgent.FEI_YUE );
		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );
		String signStr = this.assemblyUrl( bodyMap ) + signMd5;

		String sign = DigestUtils.md5Hex( signStr );
		bodyMap.put( "sign", sign );

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( bodyMap, httpHeaders );

		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.postForObject( payAgentPlatform.getPayOrderAddr(), httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			reqPayAgent.setFailReason( e.getMessage() );
		}
		if ( !CollectionUtils.isEmpty( resultMap ) ) {
			if ( "0".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
				log.info( "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
				return true;
			} else {
				reqPayAgent.setFailReason( resultMap.getOrDefault( "msg", "" ).toString() );

				payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
			}
		}
		log.warn( "代付订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
		if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
			log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
			return "fail";
		}
		String sign = requestMap.remove( "sign" ).toString();

		SortedMap<String, Object> signMap = new TreeMap<>( requestMap );
		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

		String signStr = this.assemblyUrl( signMap ) + signMd5;
		log.info( signStr );
		String mySign = DigestUtils.md5Hex( signStr );
		if ( org.apache.commons.lang3.StringUtils.equalsIgnoreCase( sign, mySign ) ) {
			String orderStatus = signMap.getOrDefault( "orderStatus", "" ).toString();
			String outTradeNo  = signMap.getOrDefault( "outTradeNo", "" ).toString();
			if ( "2".equals( orderStatus ) || "3".equals( orderStatus ) ) {
				MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( outTradeNo );
				if ( withdrawLog == null ) {
					log.error( "提现相关记录丢失 - merOrderNo:{}", outTradeNo );
					return "fail";
				}
				if ( withdrawLog.getStatus() == 6 ) {
					log.error( "已有代付记录 - merOrderNo:{}", outTradeNo );
					return "SUCCESS";
				}
				PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( outTradeNo );
				payAgentService.processOrderPay( withdrawLog, payAgentLog, outTradeNo, payAgentPlatform,
						"3".equals( orderStatus ) );
				return "SUCCESS";
			}
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
		MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
		PayAgentPlatform payAgentPlatform =
				payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
		SortedMap<String, String> bodyMap = new TreeMap<>();
		bodyMap.put( "outTradeNo", withdrawLog.getOrderNo() );
		bodyMap.put( "userId", payAgentPlatform.getMerId() );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

		// 生成签名信息
		String signStr = this.assemblyUrl( bodyMap ) + signMd5;
		String sign    = DigestUtils.md5Hex( signStr );
		bodyMap.put( "sign", sign );

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( bodyMap, httpHeaders );

		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.postForObject( payAgentPlatform.getPayOrderQueryAddr(), httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		log.warn("飞跃代付订单查询结果" + JsonUtil.object2Json( resultMap ));
		if ( !CollectionUtils.isEmpty( resultMap )) {
			if ("0".equals(resultMap.getOrDefault("code", "").toString())) {
				Map<String, Object> resultDataMap = (Map<String, Object>) resultMap.getOrDefault("data", new HashMap<>());
				int orderState = Integer.parseInt(resultDataMap.getOrDefault("orderStatus", -1).toString());
				// status 4代付中5代付失败6代付成功
				// orderState (0待处理 1处理中 2处理失败 3处理成功)
				int status = 4;
				switch (orderState) {
					case 3:
						status = 6;
						break;
					case 2:
						status = 5;
						break;
					default:
						break;
				}
				payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState);
			}
			return JsonUtil.object2Json(resultMap);
		}
		return "飞跃代付查询失败,订单号:"+withdrawLog.getOrderNo();
	}
}
