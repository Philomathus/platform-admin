package com.qiqilm.server.admin.domain.rsp;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.utils.StringUtils;
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
	 * 派奖
	 */
	@Excel( name = "派奖", sort = 4 )
	private BigDecimal prize;

	/**
	 * 投注
	 */
	@Excel( name = "投注", sort = 3 )
	private BigDecimal cost;

	/**
	 * 主播ID
	 */
	@Excel( name = "主播ID", sort = 1 )
	private Integer anchor;

	/**
	 * 主播昵称
	 */
	@Excel( name = "主播昵称", sort = 2 )
	private String nickName;

	@Excel( name = "日期", sort = 6 )
	private String updateTime;

	@Excel( name = "派奖千六", sort = 5 )
	private BigDecimal prizeSixThousand;
	private Integer    id;

	public void setUpdateTime( String updateTime ) {
		if ( StringUtils.isNotBlank( updateTime ) ) {
			this.updateTime = updateTime.split( " " )[ 0 ];
		}
	}
}