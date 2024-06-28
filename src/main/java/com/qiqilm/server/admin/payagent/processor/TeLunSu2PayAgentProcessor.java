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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository( value = ConstantsPayAgent.TE_LUN_SU2 + "PayAgentProcessor" )
@Log4j2
public class TeLunSu2PayAgentProcessor extends AbstractPayAgent {
	@Override
	public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		Map<String, String> dataMap = new LinkedHashMap<>();
		dataMap.put( "Amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).toString() );
		dataMap.put( "BankNumber", withdrawLog.getBankAccount() );
		dataMap.put( "BankAcc", withdrawLog.getBankUserName() );
		dataMap.put( "orderNo", withdrawLog.getOrderNo() );
		dataMap.put( "MsgUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
		dataMap.put( "Bank", withdrawLog.getBankName() );
		dataMap.put( "mch_id", payAgentPlatform.getMerId() );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

		String signStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
		String sign    = DigestUtils.md5Hex( signStr ).toUpperCase();
		dataMap.put( "sign", sign );
		MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll( dataMap );
		log.warn( JsonUtil.object2Json( requestMap ) );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );

		Map<String, Object> resultMap  = null;
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
		}
		log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
		if ( !CollectionUtils.isEmpty( resultMap ) ) {
			if ( "success".equals( resultMap.getOrDefault( "msg", "" ).toString() ) ) {
				log.warn( "特仑苏2代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
				return true;
			}else{
				reqPayAgent.setFailReason(resultMap.getOrDefault("Chinese","").toString());
				payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
			}
		}
		log.warn( "特仑苏2代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
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
		log.info( "特仑苏2回调待签名字符串:" + requestMap );
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
	public String queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
		MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
		PayAgentPlatform payAgentPlatform =
				payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
		Map<String, String> dataMap = new LinkedHashMap<>();
		dataMap.put( "orderNo", withdrawLog.getOrderNo() );
		dataMap.put( "mch_id", payAgentPlatform.getMerId() );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
		String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
		String sign    = DigestUtils.md5Hex( tempStr ).toUpperCase();
		dataMap.put( "sign", sign );

		MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll( dataMap );
		log.warn( JsonUtil.object2Json( requestMap ) );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>( requestMap, httpHeaders );

		Map<String, Object> resultMap  = null;
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
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		log.warn( "特仑苏2查询结果:" + JsonUtil.object2Json(resultMap) );
		if ( !CollectionUtils.isEmpty( resultMap ) ) {
			if ( "success".equals( resultMap.getOrDefault( "code", "" ).toString() ) ) {
				Map    dataMapRsp = ( Map ) resultMap.get( "data" );
				String orderNo    = dataMapRsp.getOrDefault( "orderNo", "" ).toString();
				String state      = dataMapRsp.getOrDefault( "state", "" ).toString();
				String money      = dataMapRsp.getOrDefault( "money", "" ).toString();
				String mch_id     = dataMapRsp.getOrDefault( "mch_id", "" ).toString();
				String sign1      = resultMap.getOrDefault( "sign", "" ).toString();

				Map<String, Object> rspMap = new LinkedHashMap<>();
				rspMap.put( "orderNo", orderNo );
				rspMap.put( "state", state );
				rspMap.put( "money", money );
				rspMap.put( "mch_id", mch_id );
				String md5Str  = this.assemblyUrl( rspMap ) + "&key=" + signMd5;
				String rspSign = DigestUtils.md5Hex( md5Str ).toUpperCase();
				if ( sign1.equals( rspSign ) && !"wait".equals( state ) ) {
					log.warn( "特仑苏2验签通过 - orderNo:{}", orderNo );
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
				}
			}
			return resultMap.getOrDefault("msg", "").toString();
		}
		return "特仑苏2代付查询失败,订单号:"+withdrawLog.getOrderNo();
	}
}
