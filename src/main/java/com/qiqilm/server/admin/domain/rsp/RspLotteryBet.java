package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * lottery_bet
 *
 * @author
 */
@Data
public class RspLotteryBet {

	private Byte status;

	/**
	 * 中奖金额
	 */
	private BigDecimal prize;

	/**
	 * 投资
	 */
	private BigDecimal cost;

	/**
	 * 主播ID
	 */
	private Integer anchor;

	/**
	 * 主播昵称
	 */

	private String nickName;


	private String updateTime;

	private BigDecimal prizeSixThousand;
	private Integer id;
}