package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.BIHAI + "PayAgentProcessor" )
@Log4j2
public class BiHaiPayAgentProcessor extends AbstractPayAgent {
	@Override
	public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		Map<String, String> dataMap = new TreeMap<>();
		long                time    = System.currentTimeMillis() / 1000;
		dataMap.put( "timestamp", String.valueOf( time ) );
		dataMap.put( "accesskey", payAgentPlatform.getMerId() );
		dataMap.put( "bankname", withdrawLog.getBankName() );
		dataMap.put( "realname", withdrawLog.getBankUserName().trim() );
		dataMap.put( "money", withdrawLog.getWithdrawMoney().toString() );
		dataMap.put( "cardnumber", withdrawLog.getBankAccount().trim() );
		dataMap.put( "orderid", withdrawLog.getOrderNo() );
		dataMap.put( "callbackurl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
		dataMap.put( "ext", "ext" );
		StringBuilder stringBuilder = new StringBuilder();
		dataMap.forEach( ( k, v ) -> stringBuilder.append( v ) );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

		String sign = stringBuilder.toString() + signMd5;
		sign = DigestUtils.md5Hex( sign );
		dataMap.put( "sign", sign );
		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.postForObject( payAgentPlatform.getPayOrderAddr(), dataMap, Map.class );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
		int code = ( int ) resultMap.get( "Code" );
		if ( code == 0 ) {
			resultMap = ( Map<String, Object> ) resultMap.get( "Data" );
			if ( !CollectionUtils.isEmpty( resultMap ) ) {
				code = ( int ) resultMap.get( "Status" );
				if ( code != 8 && code != 16 ) {
					log.warn("碧海代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
					return true;
				}
			}
		}
		log.warn("碧海代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
		if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
			log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
			return "fail";
		}
		String                    orderId = ( String ) requestMap.get( "OrderId" );
		SortedMap<String, Object> map     = new TreeMap<>( requestMap );
		String                    sign    = ( String ) map.remove( "Sign" );
		map.remove( "Ext" );
		StringBuilder stringBuilder = new StringBuilder();
		map.forEach( ( k, v ) -> stringBuilder.append( v ) );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

		String mySign = stringBuilder.toString() + signMd5;
		mySign = DigestUtils.md5Hex( mySign );
		if ( org.apache.commons.lang3.StringUtils.equals( sign, mySign ) ) {
			int status = ( int ) requestMap.get( "Status" );

			MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderId );
			if ( withdrawLog == null ) {
				log.error( "提现相关记录丢失 - merOrderNo:{}", orderId );
				return "fail";
			}
			if ( withdrawLog.getStatus() == 6 ) {
				log.error( "已有代付记录 - merOrderNo:{}", orderId );
				return "200";
			}
			PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderId );
			payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, status == 4 );
			return "200";
		}
		return "fail";
	}

	@Override
	public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
													 String realIp ) {
		return null;
	}

	@Override
	public String queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
		MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
		PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());
		Map<String, Object> paramsMap = new TreeMap<>();
		paramsMap.put("merchant_order_sn", withdrawLog.getOrderNo());
		paramsMap.put("merchant_sn", payAgentPlatform.getMerId());

		String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey"));

		String tempStr = this.assemblyUrl(paramsMap) + "&key=" + signMd5;
		String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
		paramsMap.put("sign", sign);

		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll(paramsMap);
		log.warn(JsonUtil.object2Json(requestMap));
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

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
			log.info("碧海代付查询结果 - result:{}", JsonUtil.object2Json(resultMap));
			if (!CollectionUtils.isEmpty(resultMap)) {
				int code = Integer.parseInt(resultMap.getOrDefault("code", 0).toString());
				if (code == 1) {
					Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
					int statusType = Integer.parseInt(dataMap.getOrDefault("status", 0).toString());
					// status 4代付中 5代付失败 6代付成功
					// statusType 0：待处理， 1：处理中， 2：已打款， 3：已拒绝 ， 4：已退单
					int status = 4;
					if (statusType == 2) {
						status = 6;
						statusType = 2;
					} else if (statusType == 3 || statusType == 4) {
						status = 5;
						statusType = 3;
					} else {
						statusType = 0;
					}
					payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType);
				}
				return resultMap.getOrDefault("msg", "").toString();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "碧海代付查询失败,订单号:" + withdrawLog.getOrderNo();
	}

	/*public static void main(String[] args) {
		Map<String, String> dataMap = new TreeMap<>();
		long                time    = System.currentTimeMillis() / 1000;
		dataMap.put( "timestamp", String.valueOf( time ) );
		dataMap.put( "accesskey", "3eyezkzk14tx90oBl2LGHMV0x7" );
		dataMap.put( "bankname", withdrawLog.getBankName() );
		dataMap.put( "realname", withdrawLog.getBankUserName().trim() );
		dataMap.put( "money", withdrawLog.getWithdrawMoney().toString() );
		dataMap.put( "cardnumber", withdrawLog.getBankAccount().trim() );
		dataMap.put( "orderid", withdrawLog.getOrderNo() );
		dataMap.put( "callbackurl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
		dataMap.put( "ext", "ext" );
		StringBuilder stringBuilder = new StringBuilder();
		dataMap.forEach( ( k, v ) -> stringBuilder.append( v ) );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

		String sign = stringBuilder.toString() + signMd5;
		sign = DigestUtils.md5Hex( sign );
		dataMap.put( "sign", sign );
		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.postForObject( payAgentPlatform.getPayOrderAddr(), dataMap, Map.class );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		log.warn( JsonUtil.object2Json( resultMap ) );
		int code = ( int ) resultMap.get( "Code" );
		if ( code == 0 ) {
			resultMap = ( Map<String, Object> ) resultMap.get( "Data" );
			if ( !CollectionUtils.isEmpty( resultMap ) ) {
				code = ( int ) resultMap.get( "Status" );
				if ( code != 8 && code != 16 ) {
					return true;
				}
			}
		}
		log.warn( "代付订单提交失败" );
		return false;
	}*/
}
