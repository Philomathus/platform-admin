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
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository( value = ConstantsPayAgent.TE_LUN_SU + "PayAgentProcessor" )
@Log4j2
public class TeLunSuPayAgentProcessor extends AbstractPayAgent {
	@Override
	public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		Map<String, String> dataMap = new LinkedHashMap<>();
		dataMap.put( "Amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).toString() );
		dataMap.put( "BankNumber", withdrawLog.getBankAccount() );
		dataMap.put( "BankAcc", withdrawLog.getBankUserName() );
		dataMap.put( "orderNo", withdrawLog.getOrderNo() );
		dataMap.put( "MsgUrl", "http://47.57.230.214:43007/pay-agent/callBack/" + ConstantsPayAgent.TE_LUN_SU );
		dataMap.put( "Bank", withdrawLog.getBankName() );
		dataMap.put( "mch_id", payAgentPlatform.getMerId() );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

		String signStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
		String sign    = DigestUtils.md5Hex( signStr ).toUpperCase();
		dataMap.put( "sign", sign );
		MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll( dataMap );
		log.warn( JsonUtil.object2Json( requestMap ) );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );
		String                                    res  = null;
		try {
			res = restTemplate.postForObject( payAgentPlatform.getPayOrderAddr(), httpEntity, String.class );
			log.info( "特仑苏2代付下单结果" + res );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		if ( StringUtils.isNotBlank( res ) ) {
			Map map = JsonUtil.json2Map( res );
			if ( "success".equals( map.getOrDefault( "msg", "" ).toString() ) ) {
				return true;
			} else {
				reqPayAgent.setFailReason( map.get( "msg" ).toString() );
			}
		}
		log.warn( "代付订单提交失败 - result:{}", JsonUtil.object2Json( res ) );
		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );
		String              orderNo = ( String ) requestMap.get( "orderNo" );
		String              state   = ( String ) requestMap.get( "state" );
		String              money   = ( String ) requestMap.get( "money" );
		String              mch_id  = ( String ) requestMap.get( "mch_id" );
		Map<String, String> dataMap = new LinkedHashMap<>();
		dataMap.put( "orderNo", orderNo );
		dataMap.put( "state", state );
		dataMap.put( "money", money );
		dataMap.put( "mch_id", mch_id );
		String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
		log.info( "特仑苏回调待签名字符串:" + requestMap );
		String sign    = ( String ) requestMap.get( "sign" );
		String signMD5 = DigestUtils.md5Hex( tempStr ).toUpperCase();
		if ( sign.equalsIgnoreCase( signMD5 ) ) {
			MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( orderNo );
			if ( withdrawLog == null ) {
				log.error( "提现相关记录丢失 - merOrderNo:{}", orderNo );
				return "fail";
			}
			if ( withdrawLog.getStatus() == 6 ) {
				log.error( "已有代付记录 - merOrderNo:{}", orderNo );
				return "success";
			}
			PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( orderNo );
			payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "success".equals( state ) );
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
	public void queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
		MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
		PayAgentPlatform payAgentPlatform =
				payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
		Map<String, String> dataMap = new LinkedHashMap<>();
		dataMap.put( "orderNo", withdrawLog.getOrderNo() );
		dataMap.put( "mch_id", payAgentPlatform.getMerId() );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );
		String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
		String sign    = DigestUtils.md5Hex( tempStr ).toUpperCase();
		dataMap.put( "sign", sign );

		MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll( dataMap );

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );

		String result = null;
		try {
			result = restTemplate.postForObject( payAgentPlatform.getPayOrderQueryAddr(), httpEntity, String.class );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		log.warn( "特仑苏查询结果:" + result );
		if ( Strings.isNotBlank( result ) ) {
			Map map = JsonUtil.json2Map( result );
			if ( "success".equals( map.getOrDefault( "code", "" ).toString() ) ) {

				Map    dataMapRsp = ( Map ) map.get( "data" );
				String orderNo    = dataMapRsp.getOrDefault( "orderNo", "" ).toString();
				String state      = dataMapRsp.getOrDefault( "state", "" ).toString();
				String money      = dataMapRsp.getOrDefault( "money", "" ).toString();
				String mch_id     = dataMapRsp.getOrDefault( "mch_id", "" ).toString();
				String sign1      = map.getOrDefault( "sign", "" ).toString();

				Map<String, Object> rspMap = new LinkedHashMap<>();
				rspMap.put( "orderNo", orderNo );
				rspMap.put( "state", state );
				rspMap.put( "money", money );
				rspMap.put( "mch_id", mch_id );
				String md5Str  = this.assemblyUrl( rspMap ) + "&key=" + signMd5;
				String rspSign = DigestUtils.md5Hex( md5Str ).toUpperCase();
				if ( sign1.equals( rspSign ) && !"wait".equals( state ) ) {
					log.warn( "特仑苏验签通过 - orderNo:{}", orderNo );
					int status     = 4;
					int orderState = 0;
					if ( "success".equals( state ) ) {
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
		}
	}
}
