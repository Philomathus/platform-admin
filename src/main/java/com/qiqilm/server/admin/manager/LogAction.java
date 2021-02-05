package com.qiqilm.server.admin.manager;

import com.qiqilm.server.admin.enums.EnumAction;
import com.qiqilm.server.admin.service.ILogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 会员行为日志个管理
 *
 * @see com.qiqilm.server.admin.enums.EnumAction
 */
@Component
public class LogAction {
	@Autowired
	private ILogService logService;

	@Async
	public void logXiafen( String userId, String account, String platformname, String orderId,
						   BigDecimal changeMoney, BigDecimal total, BigDecimal backMoney ) {
		logService.logMemberAction( userId, account, EnumAction.xiafen, "盈利：" + changeMoney.setScale( 2,
				BigDecimal.ROUND_HALF_UP ).toString(), "余额" + total.toString(),
				"平台：" + platformname + "下分金额" + backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ).toString(),
				"下分订单号：" + orderId );
	}
}
