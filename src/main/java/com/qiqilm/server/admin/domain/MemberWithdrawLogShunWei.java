package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
public class MemberWithdrawLogShunWei {
	private static final long serialVersionUID = 1L;

	/**
	 * 提现银行
	 */
	private String bankName;

	/**
	 * 收款人
	 */
	private String bankUserName;

	/**
	 * 提现账号
	 */
	private String bankAccount;

	/**
	 * 提现金额
	 */
	private Integer withdrawMoney;

	private String orderNo;

}
