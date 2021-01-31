package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;

import java.util.Date;

public interface IPayAgentService {
	void queryAgent4Status5Min();

	AjaxResult payAgentOrder( ReqPayAgent reqPayAgent ) throws Exception;

	void processOrderPay( String merOrderNo, String orderNo, PayAgentPlatform payAgentPlatform, boolean isSuccess );

	void processOrder( PayAgentPlatform payAgentPlatform, MemberWithdrawLog memberWithdrawLog,
					   Date now, int status, int orderState );
}
