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
import com.qiqilm.server.admin.utils.StringUtils;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Repository( value = ConstantsPayAgent.XINHUI + "PayAgentProcessor" )
@Log4j2
public class XinHuiPayAgentProcessor extends AbstractPayAgent {

	@Override
	public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		Map<String, Object> dataMap = new LinkedHashMap<>();
		dataMap.put( "point", withdrawLog.getWithdrawMoney().setScale(2,BigDecimal.ROUND_HALF_UP) );
		dataMap.put( "bank_name", withdrawLog.getBankName() );
		dataMap.put( "bank_branch_name", withdrawLog.getBankName() );
		dataMap.put( "bank_account_name", withdrawLog.getBankUserName() );
		dataMap.put( "bank_account_number", withdrawLog.getBankAccount() );
		dataMap.put( "merchant_order_sn", withdrawLog.getOrderNo() );

		String merchantId = payAgentPlatform.getMerId();
		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
		String authStr = Base64.getEncoder().encodeToString(String.format("%s:%s", merchantId, signMd5).getBytes(StandardCharsets.UTF_8));

		List<Map<String, Object>> list = new LinkedList<>();
		list.add(dataMap);
		log.warn( payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json( list ) );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Authorization","Basic "+authStr);
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<List<Map<String, Object>>> httpEntity = new HttpEntity<>( list, httpHeaders );

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
		}
		log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );

		if ( !CollectionUtils.isEmpty( resultMap ) ) {
			if ( "0".equals( resultMap.getOrDefault( "error", "" ).toString() ) ) {
				Map result = (Map)resultMap.getOrDefault( "result", "" );
				if("1".equals(result.getOrDefault( "success", "" ).toString())){
					log.info( payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json( resultMap ) );
					return true;
				} else {
					reqPayAgent.setFailReason( JsonUtil.object2Json( result ) );
				}
			}
			if(StringUtils.isNotBlank( resultMap.getOrDefault( "message", "" ).toString() )){
				reqPayAgent.setFailReason( resultMap.getOrDefault( "message", "" ).toString() );
			}
			payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
		}

		log.warn( payAgentPlatform.getName() + "订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {

		String rspSign = requestMap.remove( "sign" ).toString();
		String timestamp = requestMap.remove( "timestamp" ).toString();

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
		String temStr = JsonUtil.object2Json( requestMap )+"&key="+signMd5+"&timestamp="+timestamp;
		String sign = DigestUtils.md5Hex( temStr );

		log.info( payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign );
		if ( rspSign.equalsIgnoreCase( sign ) ) {
			String order_num = requestMap.getOrDefault( "merchant_order_sn", "" ).toString();
			String status    = requestMap.getOrDefault( "status", "" ).toString();

			MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( order_num );
			if ( withdrawLog == null ) {
				log.error( "提现相关记录丢失 - merOrderNo:{}", order_num );
				return "fail";
			}
			if ( withdrawLog.getStatus() == 2 ) {
				log.error( "订单已拒绝，无需回调 - merOrderNo:{}", order_num );
				return "success";
			}
			if ( withdrawLog.getStatus() == 6 ) {
				log.error( "已有代付记录 - merOrderNo:{}", order_num );
				return "success";
			}
			PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( order_num );
			payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "审核成功".equals( status ) );
			log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "审核成功".equals( status ) ? "成功" : "失败" );
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
		MemberWithdrawLog withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
		PayAgentPlatform  payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
		String orderNo = withdrawLog.getOrderNo();

		String merchantId = payAgentPlatform.getMerId();
		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
		String authStr = Base64.getEncoder().encodeToString(String.format("%s:%s", merchantId, signMd5).getBytes(StandardCharsets.UTF_8));

		log.warn( payAgentPlatform.getName() + "查询代付状态接口请求参数{}", orderNo );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Authorization","Basic "+authStr);
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( null, httpHeaders );

		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.execute( payAgentPlatform.getPayOrderQueryAddr()+orderNo, HttpMethod.GET,
					restTemplate.httpEntityCallback( httpEntity ), response -> {
						InputStream bodyStream = response.getBody();
						String      text;
						try ( Reader reader = new InputStreamReader( bodyStream ) ) {
							text = CharStreams.toString( reader );
						}
						return JsonUtil.json2Map( text );
					} );
			log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

			if ( !CollectionUtils.isEmpty( resultMap ) ) {
				//  status
				//  4代付中 5代付失败 6代付成功
				int status = 4;

				//  statusCode
				//  ⽆需审核、审核中、审核通过、审核成功、审核驳回、审核失败
				String statusCode = null;

				String code = resultMap.getOrDefault("error", "").toString();
				if (!"0".equals(code)) {
					statusCode = "审核失败";
				}
				String result = resultMap.getOrDefault("result", "").toString();
				if (StringUtils.isNotBlank(result)) {
					Map<String, Object> map = (Map<String, Object>) resultMap.getOrDefault("result", "");
					if (!CollectionUtils.isEmpty(map)) {
						statusCode = map.getOrDefault("status", "").toString();
					}

					if ("审核通过".equals(statusCode) || "审核成功".equals(statusCode) || "审核驳回".equals(statusCode) || "审核失败".equals(statusCode)) {
						if ("审核通过".equals(statusCode) || "审核成功".equals(statusCode)) {
							status = 6;
						} else {
							status = 5;
						}
						payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
								status);
					}
					return resultMap.getOrDefault("message", "").toString();
				}
			}
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
	}

}
