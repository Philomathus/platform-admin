package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;

import java.math.RoundingMode;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.HUA_ZI + "PayAgentProcessor" )
@Log4j2
public class HuaZiPayAgentProcessor extends AbstractPayAgent {
	@Override
	public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		SortedMap<String, Object> bodyMap = new TreeMap<>();
		bodyMap.put( "merid", withdrawLog.getOrderNo() );
		bodyMap.put( "merOrderNo", withdrawLog.getOrderNo() );
		bodyMap.put( "amount", withdrawLog.getWithdrawMoney().setScale( 0, RoundingMode.HALF_UP ) );
		bodyMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + ConstantsPayAgent.HENG_XIN );
		bodyMap.put( "bankCode", withdrawLog.getBankCode() );
		bodyMap.put( "submitTime", reqPayAgent.getCurrentTime().getTime() );
		bodyMap.put( "bankAccountNo", withdrawLog.getBankAccount() );
		bodyMap.put( "bankAccountName", withdrawLog.getBankUserName() );
		String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
				"secretkey/payAgentPrivateKey" ) );
		String signStr = this.assemblyUrl( bodyMap ) + "&key=" + signMd5;

		String sign = DigestUtils.md5Hex( signStr ).toUpperCase();
		bodyMap.put( "sign", sign );

		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
		return null;
	}

	@Override
	public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
													 String realIp ) throws Exception {
		return null;
	}

	@Override
	public void queryOrderPay( PayAgentLog payAgentLog ) throws Exception {

	}
}
