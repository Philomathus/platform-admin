package com.qiqilm.server.admin.payagent.processor;


import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeShunWeiType;
import com.qiqilm.server.admin.exception.BusinessException;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.RoundingMode;
import java.util.*;

@Repository( value = ConstantsPayAgent.NIU_QI_CHONG_TIAN + "PayAgentProcessor" )
@Log4j2
public class NiuQiChongTianPayAgentProcessor extends AbstractPayAgent  {
	@Override
	public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		withdrawLog.setBankCode( "unionpay" );
		List list=new ArrayList();
		Map mapList=new LinkedHashMap();
		mapList.put( "amount", withdrawLog.getWithdrawMoney().setScale( 2, RoundingMode.HALF_UP ).toString() );
		mapList.put( "accountname", withdrawLog.getBankUserName() );
		mapList.put( "bankname", withdrawLog.getBankName().trim() );
		mapList.put( "cardnumber", withdrawLog.getBankAccount().trim() );
		mapList.put( "subbranch","" );
		mapList.put( "province", "");
		mapList.put( "city", "");
		mapList.put( "mobile", "" );
		mapList.put("out_trade_no", withdrawLog.getOrderNo() );
		mapList.put( "attach","" );
		mapList.put( "extends","rrr" );
		list.add(mapList);
		Map<String, Object> dataMap = new TreeMap<>();
		dataMap.put( "mchid", payAgentPlatform.getMerId() );
		dataMap.put( "addtime", System.currentTimeMillis()+"");
		dataMap.put( "bankcode", withdrawLog.getBankCode() );
		dataMap.put( "list", JsonUtil.object2Json(list) );
		dataMap.put( "callback_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + ConstantsPayAgent.NIU_QI_CHONG_TIAN );
		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );
		String tempStr = this.assemblyUrl( dataMap ) +"&key="+signMd5;
		String sign = DigestUtils.md5Hex( tempStr).toUpperCase();
		dataMap.put( "sign", sign );

		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll( dataMap );
		log.warn( JsonUtil.object2Json( requestMap ) );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );
		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.postForObject( payAgentPlatform.getPayOrderAddr(), httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		if ( !CollectionUtils.isEmpty( resultMap ) ) {
			if ( "success".equals( resultMap.getOrDefault( "status", "" ).toString() )) {
				Map<String, Object> result = ( Map) resultMap.get( "data" );
				String status = result.getOrDefault("status", "").toString();
				String success = result.getOrDefault("success", "").toString();
				if ("1".equals(status)&&"1".equals(success)){
					List<Map<String,Object>> listResult= (List<Map<String, Object>>) result.getOrDefault("list", new ArrayList<>());
					log.info( "代付订单提交成功 - listResult:{}", JsonUtil.object2Json( listResult ) );
					for (Map map:listResult) {
						String outTradeNo = (String) map.getOrDefault("out_trade_no", "");
						String statusRsp = map.getOrDefault("status", "").toString();
						if ("1".equals(statusRsp)&&withdrawLog.getOrderNo().equals(outTradeNo)){
							log.info( "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
							return true;
						}

					}
				}
			}
		}
		log.warn( "代付订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {

		String     rspSign = requestMap.remove("sign").toString();
		SortedMap<String, Object> bodyMap          = new TreeMap<>(requestMap);
		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );
		String tempStr = this.assemblyUrl( bodyMap ) +"&key="+signMd5;
		log.info( "牛气冲天回调待签名字符串:" + requestMap );
		String sign = DigestUtils.md5Hex( tempStr).toUpperCase();
		if ( ( rspSign ).equalsIgnoreCase( sign ) ) {
			String order_num    = ( String ) requestMap.get( "out_trade_no" );
			String remit_result = ( String ) requestMap.get( "status" );

			MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( order_num );
			if ( withdrawLog == null ) {
				log.error( "提现相关记录丢失 - merOrderNo:{}", order_num );
				return "fail";
			}
			if ( withdrawLog.getStatus() == 6 ) {
				log.error( "已有代付记录 - merOrderNo:{}", order_num );
				return "ok";
			}
			PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( order_num );
			payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals( remit_result ) );
			return "ok";
		}
		return "fail";
	}

	@Override
	public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
		return null;
	}


	@Override
	public void queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
		MemberWithdrawLog   withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
		PayAgentPlatform    payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
		Map<String, Object> dataMap          = new TreeMap<>();
		dataMap.put( "mchid", payAgentPlatform.getMerId() );
		dataMap.put( "out_trade_no", withdrawLog.getOrderNo() );
		dataMap.put( "applytime", System.currentTimeMillis()+"" );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );
		String tempStr = this.assemblyUrl( dataMap ) +"&key="+signMd5;
		String sign = DigestUtils.md5Hex( tempStr).toUpperCase();
		dataMap.put( "sign", sign );

		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll( dataMap );
		log.warn( JsonUtil.object2Json( requestMap ) );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );
		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.postForObject( payAgentPlatform.getPayOrderQueryAddr(), httpEntity, Map.class );
			if (!CollectionUtils.isEmpty(resultMap)&&"1".equals(resultMap.getOrDefault( "status", "" ).toString())){
				String statusCode = String.valueOf( resultMap.getOrDefault( "status", "" ).toString());
				int    status     = 4;
				int    orderState = 0;
				if ( "1".equals( statusCode ) ) {
					status = 6;
					orderState = 1;
				} else {
					status = 5;
					orderState = 2;
				}
				payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState );
				return;
			}
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}

	}
}
