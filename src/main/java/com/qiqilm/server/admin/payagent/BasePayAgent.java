package com.qiqilm.server.admin.payagent;

import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;

import java.util.Map;

public interface BasePayAgent {
	/**
	 * 代付下单
	 *
	 * @param withdrawLog      提现记录表
	 * @param payAgentPlatform 代付平台表
	 * @param reqPayAgent      下单数据，包括代付平台ID和提现记录订单号
	 * @return 代付下单是否成功
	 */
	boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception;

	/**
	 * 代付回调
	 *
	 * @param requestMap 代付方回调过来的数据
	 * @param realIp     代付方回调IP
	 * @return 是否成功文本
	 */
	String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception;

	/**
	 * 代付反查
	 *
	 * @param requestMap 代付方反查过来的数据
	 * @param realIp     代付方反查回调IP
	 * @return 代付方反查需要对象
	 */
	Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception;

	/**
	 * 代付查询
	 */
	String queryOrderPay( PayAgentLog payAgentLog ) throws Exception;
}
