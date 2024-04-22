package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户投资行为对象 lottery_bet0
 *
 * @author 77tv
 * @date 2021-03-03
 */
@Data
public class LotteryBet0 extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/** id */
	private String id;

	/** 下注彩种id */
	@Excel( name = "下注彩种id" )
	private Long lotteryId;

	/** 平台用户ID */
	@Excel( name = "平台用户ID" )
	private String puserId;

	/** 下注期数 */
	@Excel( name = "下注期数" )
	private String issue;

	/** 0= 待开奖 1= 已中奖 2=未中奖 */
	@Excel( name = "0= 待开奖 1= 已中奖 2=未中奖" )
	private Long status;

	/** 下注选择菜单 */
	@Excel( name = "下注选择菜单" )
	private String methodId;

	/** 下注选择 */
	@Excel( name = "下注选择" )
	private String betSelect;

	/** 下注索引 */
	@Excel( name = "下注索引" )
	private String betIds;

	/** 筹码 */
	@Excel( name = "筹码" )
	private BigDecimal chip;

	/** 中奖金额 */
	@Excel( name = "中奖金额" )
	private BigDecimal prize;

	/** 投资 */
	@Excel( name = "投资" )
	private BigDecimal cost;

	/** 彩票名称 */
	@Excel( name = "彩票名称" )
	private String lotteryName;

	/** 主播ID(直播间外-1) */
	@Excel( name = "主播ID(直播间外-1)" )
	private Long anchor;

	/** 开奖号码 */
	@Excel( name = "开奖号码" )
	private String code;

	/** 下注时间 */
	@JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
	@Excel( name = "下注时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss" )
	private Date betTime;

	@JsonIgnore
	private String[] selectDate;
	@JsonIgnore
	private String   startTime;
	@JsonIgnore
	private String   endTime;
	@JsonIgnore
	private String   tableLast;
	@JsonIgnore
	private boolean  abnormal = false;

	private BigDecimal totalCost;
	private BigDecimal totalPrize;

	@JsonIgnore
	private String priceMin;
	@JsonIgnore
	private String priceMax;

	private String methodStr;

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "lotteryId", getLotteryId() )
				.append( "puserId", getPuserId() )
				.append( "issue", getIssue() )
				.append( "status", getStatus() )
				.append( "methodId", getMethodId() )
				.append( "betSelect", getBetSelect() )
				.append( "betIds", getBetIds() )
				.append( "chip", getChip() )
				.append( "prize", getPrize() )
				.append( "cost", getCost() )
				.append( "lotteryName", getLotteryName() )
				.append( "anchor", getAnchor() )
				.append( "code", getCode() )
				.append( "betTime", getBetTime() )
				.append( "updateTime", getUpdateTime() )
				.toString();
	}
}
