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
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.TIAN_TIAN + "PayAgentProcessor" )
@Log4j2
public class TianTianPayAgentProcessor extends AbstractPayAgent {
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
		dataMap.put( "callbackurl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + ConstantsPayAgent.TIAN_TIAN );
		dataMap.put( "ext", "ext" );
		StringBuilder stringBuilder = new StringBuilder();
		dataMap.forEach( ( k, v ) -> stringBuilder.append( v ) );

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

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

		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );

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
	public void queryOrderPay( PayAgentLog payAgentLog ) throws Exception {

	}
}
