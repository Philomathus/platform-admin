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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
		dataMap.put( "callbackurl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

		String sb = "parter=" + payAgentPlatform.getMerId() + "&type=unionpay&value=" +
				withdrawLog.getWithdrawMoney().setScale( 2, BigDecimal.ROUND_HALF_UP ).toString() +
				"&orderid=" + withdrawLog.getOrderNo() + "&callbackurl=" +
				sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
		String sign = DigestUtils.md5Hex( sb );
		dataMap.put( "sign", sign );
		dataMap.put( "hrefbackurl", "" );
		dataMap.put( "accountname", withdrawLog.getBankUserName().trim() );
		dataMap.put( "cardnumber", withdrawLog.getBankAccount().trim() );
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
		log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
		if(!CollectionUtils.isEmpty(resultMap)) {
			if (StringUtils.equals("0", dataMap.get("status"))) {
				log.warn("咪咪代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ));
				return true;
			} else {
				reqPayAgent.setFailReason(dataMap.getOrDefault("msg", "").toString());
			}
		}
		log.warn( "咪咪代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo() );
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

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

		String sb = "orderid=" + orderId + "&opstate=" + requestMap.get( "opstate" ) + "&ovalue=" + requestMap.get( "ovalue" ) +
				signMd5;

		log.warn( "咪咪代付回调签名字符串:{}",sign + "_" + sb );
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
	public String queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
       return "咪咪代付无查询";
	}
}
