package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 主播时长对象 live_host_wage_note
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
public class LiveHostWageNote {
	private static final long serialVersionUID = 1L;

	private Long id;

	/** 家族ID */
	private Long familyId;

	/** 主播ID */
	private Long hostId;

	/** 直播开始时间 */
	private String startTime;

	/** 直播结束时间 */
	private String endTime;

	/** 直播时长（秒） */
	private Long liveTimeSec;

	/** 主播直播结算印票 */
	private BigDecimal ticket;

	/** 历史印票总数 */
	private BigDecimal beforeTotalTicket;

	/** 彩票投注 */
	private BigDecimal cpCost;

	/** 彩票派奖 */
	private BigDecimal cpPrize;

	private String createTimes;

	private String remark;

	private BigDecimal settlementRate;
	@JsonIgnore
	private String[]   selectDate = new String[ 2 ];
	private String     familyName;
	private String     familyNickName;
	private String     nickName;
}
