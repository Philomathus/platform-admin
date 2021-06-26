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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository( value = ConstantsPayAgent.XIN_NIU_QI_CHONG_TIAN + "PayAgentProcessor" )
@Log4j2
public class XinNiuQiChongTianPayAgentProcessor extends AbstractPayAgent {
	@Override
	public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {

		Map    map       = new LinkedHashMap();
		String name      = withdrawLog.getBankUserName();
		String Card      = withdrawLog.getBankAccount().trim();
		String Bankof    = withdrawLog.getBankName().trim();
		String money     = withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).toString();
		String remarks   = withdrawLog.getOrderNo();
		String sh_id     = payAgentPlatform.getMerId();
		String notifyURL = sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + ConstantsPayAgent.XIN_NIU_QI_CHONG_TIAN;
		map.put( "name", name );
		map.put( "Card", Card );
		map.put( "Bankof", Bankof );
		map.put( "money", money );
		map.put( "remarks", remarks );
		map.put( "sh_id", sh_id );
		map.put( "notifyURL", notifyURL );
		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );
		String passWord    = payAgentPlatform.getHeaderKey();
		String passWordMd5 = DigestUtils.md5Hex( passWord );
		String tempStr     = name + Card + Bankof + money + remarks + sh_id + notifyURL + signMd5 + passWordMd5;
		String sign        = DigestUtils.md5Hex( tempStr );
		map.put( "sign", sign );

		log.warn( JsonUtil.object2Json( map ) );

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( map, httpHeaders );

		String result = null;
		try {
			result = restTemplate.postForObject( payAgentPlatform.getPayOrderAddr(), httpEntity, String.class );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		log.warn( "新牛气冲天代付下单结果 - result:{}",  result  );
		if ( Strings.isNotBlank( result ) ) {
			Map resultMap = JsonUtil.json2Map( result );
			if ( StringUtils.equals( "true", String.valueOf( resultMap.get( "result" ) ) ) ) {
				withdrawLog.setPayAgentOrderNo( resultMap.getOrDefault( "odd", "" ).toString() );
				log.warn( "新牛气冲天代付下单成功 - result:{}",  result  );
				return true;
			}
			reqPayAgent.setFailReason( resultMap.getOrDefault( "ims", "" ).toString() );
		}
		log.warn( "新牛气冲天代付订单提交失败,订单号:{}", withdrawLog.getOrderNo() );
		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
		String odd     = ( String ) requestMap.get( "odd" );
		String state   = requestMap.get( "state" ).toString();
		String sh_id   = ( String ) requestMap.get( "sh_id" );
		String beizhu  = ( String ) requestMap.get( "biezhu" );
		String rspSign = ( String ) requestMap.get( "sign" );
		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );
		String tempStr = odd + state + sh_id + beizhu + signMd5;
		log.info( "新牛气冲天回调待签名字符串:" + requestMap );
		String sign = DigestUtils.md5Hex( tempStr );
		if ( rspSign.equalsIgnoreCase( sign ) ) {

			MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( beizhu );
			if ( withdrawLog == null ) {
				log.error( "提现相关记录丢失 - merOrderNo:{}", beizhu );
				return "fail";
			}
			if ( withdrawLog.getStatus() == 6 ) {
				log.error( "已有代付记录 - merOrderNo:{}", beizhu );
				return "true";
			}
			PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( beizhu );
			payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals( state ) );
			return "true";
		}
		return "fail";
	}

	@Override
	public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
													 String realIp ) throws Exception {
		return null;
	}


	@Override
	public void queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
		MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
		PayAgentPlatform payAgentPlatform =
				payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
		Map<String, String> dataMap = new LinkedHashMap<>();
		dataMap.put( "odd", payAgentLog.getPayAgentOrderNo() );
		dataMap.put( "mchid", payAgentPlatform.getMerId() );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );
		String tempStr = payAgentLog.getPayAgentOrderNo() + payAgentPlatform.getMerId() + signMd5;
		String sign    = DigestUtils.md5Hex( tempStr );
		dataMap.put( "sign", sign );

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( dataMap, httpHeaders );

		String result = null;
		try {
			result = restTemplate.postForObject( payAgentPlatform.getPayOrderQueryAddr(), httpEntity, String.class );
			log.warn( "新牛气冲天代付查询结果" + result );
			Map resultMap = JsonUtil.json2Map( result );
			if ( !CollectionUtils.isEmpty( resultMap ) && "true".equals( resultMap.getOrDefault( "result", "" ).toString() ) ) {
				Integer statusCode = Integer.parseInt( ( String ) resultMap.get( "zt" ) );
				if ( statusCode >= 2 ) {
					int status     = 4;
					int orderState = 0;
					if ( statusCode >= 2 )
						if ( statusCode == 2 ) {
							status = 6;
							orderState = 1;
						} else {
							status = 5;
							orderState = 2;
						}
					payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
							orderState );
					return;
				}

			}
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}

	}
	//
	//	public static void main(String[] args) {
	//
	//		Map map=new LinkedHashMap();
	//		String name="fdfdsf";
	//		String Card="65464154156156156";
	//		String Bankof= "建设";
	//		String money="100.00";
	//		String remarks="415641515";
	//		String sh_id=withdrawLog.getMemberId();
	//		String notifyURL="http://daifu.36duweixin.com/sh/query.php" ;
	//		map.put( "name", name );
	//		map.put( "Card", Card );
	//		map.put( "Bankof", Bankof );
	//		map.put( "money", money );
	//		map.put( "remarks",remarks);
	//		map.put( "sh_id", sh_id);
	//		map.put( "notifyURL", notifyURL);
	//		String signMd5 = RSACoder.decryptByPrivateKey("fsdf", AuthUtil.getSecurityKeyStr(
	//				"secretkey/payAgentPrivateKey" ) );
	//		String passWord = RSACoder.decryptByPrivateKey( payAgentPlatform.getHeaderKey(), AuthUtil.getSecurityKeyStr(
	//				"secretkey/payAgentPrivateKey" ) );
	//		String passWordMd5 = DigestUtils.md5Hex(passWord);
	//		String tempStr =name+Card+Bankof+money+remarks+sh_id+notifyURL+signMd5+passWordMd5;
	//		String sign = DigestUtils.md5Hex( tempStr);
	//		map.put( "sign", sign );
	//
	//		log.warn( JsonUtil.object2Json( map ) );
	//
	//		HttpHeaders httpHeaders = new HttpHeaders();
	//		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
	//		HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( map, httpHeaders );
	//
	//		Map<String, Object> resultMap = null;
	//		try {
	//			RestTemplate restTemplate=new RestTemplate();
	//			resultMap = restTemplate.postForObject( "http://daifu.36duweixin.com/sh/query.php", httpEntity, Map.class );
	//		} catch ( Exception e ) {
	//			log.error( e.getMessage(), e );
	//		}
	//	}
}
