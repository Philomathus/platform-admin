package com.qiqilm.server.admin.payagent.processor;

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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Repository( value = ConstantsPayAgent.MIMI + "PayAgentProcessor" )
@Log4j2
public class MimiPayAgentProcessor extends AbstractPayAgent {
	@Override
	public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put( "parter", payAgentPlatform.getMerId() );
		dataMap.put( "type", "unionpay" );
		dataMap.put( "value", withdrawLog.getWithdrawMoney().setScale( 2,
				BigDecimal.ROUND_HALF_UP ).toString() );
		dataMap.put( "orderid", withdrawLog.getOrderNo() );
		dataMap.put( "callbackurl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + ConstantsPayAgent.MIMI );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

		String sb = "parter=" + payAgentPlatform.getMerId() + "&type=unionpay&value=" +
				withdrawLog.getWithdrawMoney().setScale( 2, BigDecimal.ROUND_HALF_UP ).toString() +
				"&orderid=" + withdrawLog.getOrderNo() + "&callbackurl=" +
				sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + ConstantsPayAgent.MIMI + signMd5;
		String sign = DigestUtils.md5Hex( sb );
		dataMap.put( "sign", sign );
		dataMap.put( "hrefbackurl", "" );
		dataMap.put( "accountname", withdrawLog.getBankUserName() );
		dataMap.put( "cardnumber", withdrawLog.getBankAccount() );
		dataMap.put( "bankname", withdrawLog.getBankName() );
		dataMap.put( "province", "" );
		dataMap.put( "city", "" );
		dataMap.put( "subbranch", "" );
		dataMap.put( "payerIp", "" );
		dataMap.put( "attach", "" );
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		dataMap.forEach( ( k, v ) -> map.add( k, v ) );

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>( map, httpHeaders );

		String resultStr = null;
		try {
			resultStr = restTemplate.postForObject( payAgentPlatform.getPayOrderAddr(), httpEntity, String.class );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		log.warn( JsonUtil.json2Map( resultStr ) );
		dataMap.clear();
		dataMap = JsonUtil.json2Map( resultStr );
		if ( org.apache.commons.lang3.StringUtils.equals( "0", dataMap.get( "status" ) ) &&
				org.apache.commons.lang3.StringUtils.equals( "成功", dataMap.get( "msg" ) ) ) {
			return true;
		}
		log.warn( "代付订单提交失败" );
		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
		if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
			log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
		}
		Map<String, String> map     = new HashMap<>();
		String              orderId = requestMap.get( "orderid" ).toString();
		map.put( "orderid", orderId );
		map.put( "opstate", requestMap.get( "opstate" ).toString() );
		map.put( "ovalue", requestMap.get( "ovalue" ).toString() );
		String sign = requestMap.get( "sign" ).toString();

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

		String sb = "orderid=" + orderId + "&opstate=" + requestMap.get( "opstate" ) + "&ovalue=" + requestMap.get( "ovalue" ) +
				signMd5;
		if ( org.apache.commons.lang3.StringUtils.equals( sign, DigestUtils.md5Hex( sb ) ) ) {
			int status = Integer.parseInt( requestMap.get( "opstate" ).toString() );
			MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderId );
			if ( withdrawLog == null ) {
				log.error( "提现相关记录丢失 - merOrderNo:{}", orderId );
				return "fail";
			}
			if ( withdrawLog.getStatus() == 6 ) {
				log.error( "已有代付记录 - merOrderNo:{}", orderId );
				return "opstate=0";
			}
			PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderId );
			payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, status == 0 );
			return "opstate=0";
		}
		return "fail";
	}

	@Override
	public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
													 String realIp ) {
		return null;
	}

	@Override
	public void queryOrderPay( PayAgentLog payAgentLog ) throws Exception {

	}
}
