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
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.DINGFENG + "PayAgentProcessor" )
@Log4j2
public class DingFengPayAgentProcessor extends AbstractPayAgent {
	@Override
	public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		SortedMap<String, String> bodyMap = new TreeMap<>();
		bodyMap.put( "user_id", payAgentPlatform.getMerId() );
		bodyMap.put( "item_num", withdrawLog.getWithdrawMoney().multiply( BigDecimal.valueOf( 100 ) ).setScale( 0,
				BigDecimal.ROUND_HALF_UP ).toString() );
		bodyMap.put( "bank_account", withdrawLog.getBankAccount().trim() );
		bodyMap.put( "bank_master_name", URLEncoder.encode( withdrawLog.getBankUserName().trim(), "UTF-8" ) );
		bodyMap.put( "bank_creater_name", URLEncoder.encode( withdrawLog.getBankName().trim(), "UTF-8" ) );

		String url       = sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
		String urlBase64 = Base64Encrypt( url );

		bodyMap.put( "notify_url", urlBase64 );
		bodyMap.put( "merchant_order_id", withdrawLog.getOrderNo() );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

		String tempStr = this.assemblyUrl( bodyMap ) + "&api_secret=" + signMd5;
		String signStr = DigestUtils.md5Hex( tempStr ).toLowerCase();
		String sign    = tempStr + "&sign=" + signStr;
		bodyMap.put( "sign", sign );
		//        bodyMap.remove("notify_url");
		//        bodyMap.put("notify_url",url);

		//        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
		//        requestMap.setAll(bodyMap);
		//        HttpHeaders httpHeaders = new HttpHeaders();
		//        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		//        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

		Map<String, Object> resultMap = null;
		String              urle      = payAgentPlatform.getPayOrderAddr() + "?" + sign;
		try {
			resultMap = restTemplate.getForObject( urle, Map.class );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			reqPayAgent.setFailReason( payAgentPlatform.getName() + "下单报错原因:" + e );
		}
		log.info( payAgentPlatform.getName() + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
		if ( !CollectionUtils.isEmpty( resultMap ) ) {
			String code = resultMap.getOrDefault( "code", "" ).toString();
			if ( "0".equals( code ) ) {
				log.info( payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
				return true;
			} else {
				reqPayAgent.setFailReason( resultMap.getOrDefault( "message", "" ).toString() );

				payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
			}
		}
		log.warn( payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
		String sign = requestMap.remove( "sign" ).toString();
		//（1）订单被接受时，状态为-9  订单成功时，状态为9  订单失败时，状态为-8  订单超时时，状态为-2
		String                    status  = requestMap.getOrDefault( "status", "" ).toString();
		SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

		String tempStr = this.assemblyUrl( bodyMap ) + "&api_secret=" + signMd5;
		String signStr = DigestUtils.md5Hex( tempStr );

		log.info( payAgentPlatform.getName() + "回调签名字符串:" + sign + "_" + signStr );
		if ( sign.equalsIgnoreCase( signStr ) ) {
			String merchant_order_id = ( String ) requestMap.get( "merchant_order_id" );

			MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( merchant_order_id );
			if ( withdrawLog == null ) {
				log.error( "提现相关记录丢失 - merOrderNo:{}", merchant_order_id );
				return "fail";
			}
			if ( withdrawLog.getStatus() == 6 ) {
				log.error( "已有代付记录 - merOrderNo:{}", merchant_order_id );
				return "success";
			}
			PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( merchant_order_id );
			payAgentLog.setPayAgentOrderNo( requestMap.getOrDefault( "order_id", "" ).toString() );
			payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "9".equals( status ) );
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
		MemberWithdrawLog   withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
		PayAgentPlatform    payAgentPlatform =
				payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
		Map<String, Object> paramsMap        = new TreeMap<>();
		paramsMap.put( "user_id", payAgentPlatform.getMerId() );
		paramsMap.put( "id", withdrawLog.getOrderNo() );
		paramsMap.put( "merchant_order_id", payAgentLog.getPayAgentOrderNo() );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

		String tempStr = this.assemblyUrl( paramsMap ) + "&api_secret=" + signMd5;
		String signStr = DigestUtils.md5Hex( tempStr ).toLowerCase();
		String sign    = tempStr + "&sign=" + signStr;
		paramsMap.put( "sign", sign );

		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll( paramsMap );
		log.warn( JsonUtil.object2Json( requestMap ) );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );

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
			log.info( payAgentPlatform.getName() + "查询结果- result:{}", JsonUtil.object2Json( resultMap ) );
			if ( !CollectionUtils.isEmpty( resultMap ) ) {
				String code = resultMap.getOrDefault( "code", "" ).toString();
				if ( "0".equals( code ) ) {
					Map<String, Object> dataMap    = ( Map<String, Object> ) resultMap.get( "data" );
					int                 statusType = Integer.parseInt( dataMap.getOrDefault( "status", "" ).toString() );
					if ( statusType == -8 || statusType == 9 ) {
						// status 4代付中 5代付失败 6代付成功
						// statusType -8失败，9成功，-9进行中
						int status = 4;
						if ( statusType == 9 ) {
							status = 6;
						} else {
							status = 5;
						}
						payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
								statusType );
					}
				}
				return resultMap.getOrDefault( "msg", "" ).toString();
			}
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
	}

	public static String Base64Encrypt( String contents ) {
		String s = null;
		try {
			s = URLEncoder.encode( Base64Utils.encodeToString( contents.getBytes() ), "UTF-8" );
		} catch ( UnsupportedEncodingException e ) {
			e.printStackTrace();
		}
		return s;
	}
}
