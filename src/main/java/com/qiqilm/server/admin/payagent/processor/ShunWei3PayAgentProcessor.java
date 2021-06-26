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
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Repository( value = ConstantsPayAgent.SHUN_WEI3 + "PayAgentProcessor" )
@Log4j2
public class ShunWei3PayAgentProcessor extends AbstractPayAgent {
	@Override
	public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		BankCodeShunWeiType bankCodeType = BankCodeShunWeiType.getCodeByDesc( withdrawLog.getBankName() );
		if ( bankCodeType == null ) {
			log.warn( "顺为代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName() );
			throw new BusinessException( "此代付无法支持的银行类型：" + withdrawLog.getBankName() );
		}
		withdrawLog.setBankCode( bankCodeType.name() );

		Map<String, String> dataMap = new TreeMap<>();
		dataMap.put( "client_num", payAgentPlatform.getMerId() );
		dataMap.put( "order_num", withdrawLog.getOrderNo() );
		dataMap.put( "amount", withdrawLog.getWithdrawMoney().multiply( new BigDecimal( 100 ) ).setScale( 0,
				BigDecimal.ROUND_HALF_EVEN ).toString() );
		dataMap.put( "bank_account_name", withdrawLog.getBankUserName().trim() );
		dataMap.put( "bank_account_no", withdrawLog.getBankAccount().trim() );
		dataMap.put( "bank_code", withdrawLog.getBankCode() );
		String randStr = this.generateRandNum( dataMap.size() + 1 );
		dataMap.put( "random_str", randStr );
		// 签名
		Map<String, String> paramMap = paramSort( dataMap, randStr );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

		String sign = DigestUtils.md5Hex( JsonUtil.object2Json( paramMap ).concat( signMd5 ) );

		paramMap.put( "request_sign", sign );
		paramMap.put( "callback_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + ConstantsPayAgent.SHUN_WEI3 );

		// 参数加密
		String encryptData = RSACoder.encryptByPublicKey( JsonUtil.object2Json( paramMap ),
				payAgentPlatform.getSignPublicKey() );
		// 请求参数封装
		Map<String, String> params = new HashMap<>();
		params.put( "request_body", URLEncoder.encode( encryptData, "utf-8" ) );
		params.put( "interface_version", DigestUtils.md5Hex( "1.0.0".concat( payAgentPlatform.getHeaderKey() ) ) );

		String paramsRequest = this.assemblyUrl( params );

		String result = null;
		try {
			result = request(payAgentPlatform.getPayOrderAddr(), paramsRequest, payAgentPlatform.getHeaderKey());
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		log.info( "顺为代付下单结果{}",result );
		Map<String, String> resultMap = JsonUtil.json2Map( result );
		if (!CollectionUtils.isEmpty(resultMap)) {
			if ("200".equals(resultMap.get("state_code"))) {
				resultMap.remove("state_code");
				resultMap.remove("message");
				String resultSign = resultMap.remove("sign");
				String randNum = resultMap.get("random_str");
				Map<String, String> param = paramSort(resultMap, randNum);
				String temp = JsonUtil.object2Json(param);
				String reSign = DigestUtils.md5Hex(temp.concat(signMd5));
				if (!org.apache.commons.lang3.StringUtils.equalsIgnoreCase(resultSign, reSign)) {
					return false;
				}
				log.info("顺为代付订单提交成功，orderNo：{}", withdrawLog.getOrderNo());
				return true;
			}else{
				reqPayAgent.setFailReason(resultMap.getOrDefault("message",""));
				payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
			}
		}
		log.warn( "顺为代付订单提交失败 - orderNo:{},result:{}", withdrawLog.getOrderNo(),JsonUtil.object2Json( resultMap ) );
		return false;
	}

	private String request( String url, String params, String headerKey ) {
		try {
			URL               urlObj = new URL( url );
			HttpURLConnection conn   = ( HttpURLConnection ) urlObj.openConnection();
			conn.setRequestMethod( "POST" );
			conn.setDoOutput( true );
			conn.setDoInput( true );
			conn.setUseCaches( false );
			conn.setConnectTimeout( 5000 );
			conn.setRequestProperty( "Charset", "UTF-8" );
			conn.setRequestProperty( "security_header_key", headerKey );
			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
			conn.setRequestProperty( "Content-Length", String.valueOf( params.length() ) );
			OutputStream outStream = conn.getOutputStream();
			outStream.write( params.getBytes( StandardCharsets.UTF_8 ) );
			outStream.flush();
			outStream.close();
			return getResponseBodyAsString( conn.getInputStream() );
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}
	}

	private String getResponseBodyAsString( InputStream in ) {
		try {
			BufferedInputStream buf    = new BufferedInputStream( in );
			byte[]              buffer = new byte[ 1024 ];
			StringBuilder       data   = new StringBuilder();
			int                 readDataLen;
			while ( ( readDataLen = buf.read( buffer ) ) != -1 ) {
				data.append( new String( buffer, 0, readDataLen, StandardCharsets.UTF_8 ) );
			}
			return data.toString();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

	private String generateRandNum( int size ) {
		StringBuilder randStr = new StringBuilder();
		Random        randDom = new Random();
		do {
			String tmpChar = String.valueOf( randDom.nextInt( size ) );
			if ( randStr.indexOf( tmpChar ) == -1 ) {
				randStr.append( tmpChar );
			}
		}
		while ( randStr.length() < size );
		return randStr.toString();
	}

	private Map<String, String> paramSort( Map<String, ?> map, String indexStr ) {
		Map<String, String> sortMap = new LinkedHashMap<>();
		String[]            keys    = map.keySet().toArray( new String[]{ } );
		Arrays.sort( keys );
		char[] indexs = indexStr.toCharArray();
		for ( char i : indexs ) {
			int index = Integer.parseInt( String.valueOf( i ) );
			sortMap.put( keys[ index ], map.get( keys[ index ] ).toString() );
		}
		return sortMap;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
		String                    data      = requestMap.getOrDefault( "data", "" ).toString();
		String                    str       = RSACoder.decryptByPrivateKey( data, payAgentPlatform.getSignPrivateKey() );
		Map<String, Object>       resultMap = JsonUtil.json2Map( str );
		String                    reSign    = resultMap.remove( "sign" ).toString();
		SortedMap<String, Object> signMap   = new TreeMap<>( resultMap );
		Map<String, String>       map       = paramSort( signMap, signMap.get( "random_str" ).toString() );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

		String sign = DigestUtils.md5Hex( JsonUtil.object2Json( map ).concat( signMd5 ) );
		if ( ( reSign ).equalsIgnoreCase( sign ) ) {
			String order_num    = ( String ) signMap.get( "order_num" );
			String remit_result = ( String ) signMap.get( "remit_result" );

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
			payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "SUCCESS".equals( remit_result ) );

			return "ok";
		}
		log.info( "ShunWei:" + "顺为解密失败" );
		return "fail";
	}

	@Override
	public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
													 String realIp ) throws Exception {
		if ( this.checkWhiteIp( payAgentPlatform.getPlatWhiteIpList(), realIp ) ) {
			log.warn( "请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json( requestMap ) );
		}
		log.warn( "反查数据:" + JsonUtil.object2Json( requestMap ) );
		String reSign = requestMap.remove( "sign" ).toString();

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

		String mySign = this.assemblyUrl( requestMap ) + "&key=" + signMd5;
		log.warn( mySign );
		BigDecimal amount        = new BigDecimal( requestMap.remove( "amount" ).toString() );
		String     bankAccountNo = requestMap.remove( "bankAccountNo" ).toString();
		String     clientCode    = requestMap.get( "clientCode" ).toString();
		String     clientOrderNo = requestMap.get( "clientOrderNo" ).toString();
		mySign = DigestUtils.md5Hex( mySign );
		requestMap.put( "dateTime", DateFormatUtils.formate( new Date(), DateFormatUtils.TIGHT_PATTERN_DATETIME ) );
		requestMap.put( "sign", "" );
		requestMap.put( "code", "99" );
		if ( ( reSign ).equalsIgnoreCase( mySign ) ) {
			MemberWithdrawLog memberWithdrawLog = withdrawLogMapper.selectByOrderNo( clientOrderNo );
			if ( memberWithdrawLog == null || amount.compareTo( memberWithdrawLog.getWithdrawMoney() ) != 0
					|| !bankAccountNo.equals( memberWithdrawLog.getBankAccount() )
					|| !clientCode.equals( payAgentPlatform.getMerId() ) ) {
				requestMap.put( "msg", "订单不匹配" );
				return requestMap;
			}
			requestMap.put( "code", "00" );
			requestMap.put( "msg", "验证成功" );

			mySign = this.assemblyUrl( requestMap ) + "&key=" + signMd5;
			mySign = DigestUtils.md5Hex( mySign );

			requestMap.put( "sign", mySign );
			log.warn( JsonUtil.object2Json( requestMap ) );
			return requestMap;
		}
		requestMap.put( "msg", "验签失败" );
		return requestMap;
	}

	@Override
	public void queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
		MemberWithdrawLog   withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
		PayAgentPlatform    payAgentPlatform =
				payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
		Map<String, String> dataMap          = new TreeMap<>();
		dataMap.put( "client_num", payAgentPlatform.getMerId() );
		dataMap.put( "order_num", withdrawLog.getOrderNo() );
		String randStr = generateRandNum( dataMap.size() + 1 );
		dataMap.put( "random_str", randStr );
		Map<String, String> paramMap = paramSort( dataMap, randStr );
		log.info( "签名原文串：{}", JsonUtil.object2Json( paramMap ) );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

		String sign = DigestUtils.md5Hex( JsonUtil.object2Json( paramMap ).concat( signMd5 ) );
		paramMap.put( "request_sign", sign );
		// 参数加密
		String encryptData = RSACoder.encryptByPublicKey( JsonUtil.object2Json( paramMap ),
				payAgentPlatform.getSignPublicKey() );
		// 请求参数封装
		Map<String, String> params = new HashMap<>();
		params.put( "request_body", URLEncoder.encode( encryptData, "utf-8" ) );
		params.put( "interface_version", DigestUtils.md5Hex( "1.0.0".concat( payAgentPlatform.getHeaderKey() ) ) );
		String paramsRequest = this.assemblyUrl( params );

		String result = null;
		try {
			result = request(payAgentPlatform.getPayOrderQueryAddr(), paramsRequest, payAgentPlatform.getHeaderKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.warn("顺为代付订单查询结果 - result:{}", result);
		Map<String, String> jsonObject = JsonUtil.json2Map( result );
		String              stateCode  = jsonObject.remove( "state_code" );
		if ( org.apache.commons.lang3.StringUtils.equals( "200", stateCode ) ) {
			String              resultSign = jsonObject.remove( "sign" );
			Map<String, String> signMap    = new TreeMap<>( jsonObject );
			String              randNum    = signMap.getOrDefault( "random_str", "" );
			Map<String, String> param      = paramSort( signMap, randNum );
			String              signStr    = JsonUtil.object2Json( param );
			String              reSign     = DigestUtils.md5Hex( signStr.concat( signMd5 ) );
			if ( org.apache.commons.lang3.StringUtils.equals( resultSign, reSign ) ) {
				String remit_state_code = jsonObject.getOrDefault( "remit_state_code", "" );
				// status 4代付中5代付失败6代付成功
				// orderState (0=处理中，1=成功，2=失败)
				int status     = 4;
				int orderState = 0;
				if ( "SUCCESS".equals( remit_state_code ) ) {
					status = 6;
					orderState = 1;
				}
				if ( "FAILED".equals( remit_state_code ) ) {
					status = 5;
					orderState = 2;
				}
				payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState );
				return;
			}
		}
		log.warn( "顺为代付订单查询失败 - result:{}", result );
	}
}
