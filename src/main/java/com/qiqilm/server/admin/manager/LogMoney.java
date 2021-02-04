package com.qiqilm.server.admin.manager;

import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.service.ILogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 会员交易日志管理
 *
 * @see com.qiqilm.server.admin.enums.EnumMoney
 */
@Component
public class LogMoney {
	@Autowired
	private ILogService logService;

	/**
	 * 会员退出游戏平台后，资金结算
	 */
	@Async
	public void logPlatformSwitch( String userid, String username, BigDecimal totalNow, BigDecimal old, String agent,
								   String name, String orderId ) {
		//备注行为enumTrans 现在金额totalNow   变动金额change  游戏agent  订单备注 name    变动订单号orderId
		logService.logMoneyAll( userid, username, EnumMoney.platform, totalNow, totalNow.subtract( old ), agent, name, orderId );
	}
}
