package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.enums.EnumAction;
import com.qiqilm.server.admin.enums.EnumMoney;

import java.math.BigDecimal;

public interface ILogService {
	/**
	 * 资金加备注
	 */
	void logmarkMoney( String userid, String username, EnumMoney enumTrans, BigDecimal totalNow, BigDecimal totalold,
					   String mark, String ordermk );

	/**
	 * 资金日志通用    现在金额totalNow   变动金额change   订单备注 name    变动订单号orderId
	 */
	void logMoneyAll( String userid, String username, EnumMoney enumTrans, BigDecimal totalNow, BigDecimal change, String agent,
					  String name, String orderId );

	/**
	 * 资金增加
	 */
	void logMoneyAdd( String businessId, String userid, String username, EnumMoney enumTrans, BigDecimal add, BigDecimal old,
					  String mark, String markorder );

	/**
	 * 会员行为日志
	 */
	void logMemberAction( String userid, String username, EnumAction enumAction, String params1, String params2, String params3,
						  String params4 );
}
