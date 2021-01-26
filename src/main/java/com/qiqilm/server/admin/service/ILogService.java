package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.enums.EnumMoney;

import java.math.BigDecimal;

public interface ILogService {
	/**
	 * 资金加备注
	 */
	void logmarkMoney( String userid, String username, EnumMoney enumTrans, BigDecimal totalNow, BigDecimal totalold,
					   String mark, String ordermk );
}
