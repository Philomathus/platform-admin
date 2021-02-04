package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员游戏上分信息对象 member_game_money
 *
 * @author 77tv
 * @date 2021-02-04
 */
@Data
public class MemberGameMoney extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 本地ID
	 */
	private String id;

	/**
	 * 玩家ID
	 */
	@Excel( name = "玩家ID" )
	private String memberId;

	/**
	 * 本地平台id
	 */
	@Excel( name = "本地平台id" )
	private Integer platformId;

	/**
	 * 状态
	 */
	@Excel( name = "状态" )
	private Integer status;

	/**
	 * 上分金额
	 */
	@Excel( name = "上分金额" )
	private BigDecimal money;

	/**
	 * 第一次进入时间
	 */
	@JsonFormat( pattern = "yyyy-MM-dd" )
	@Excel( name = "第一次进入时间", width = 30, dateFormat = "yyyy-MM-dd" )
	private Date ctime;

	/**
	 * 订单
	 */
	@Excel( name = "订单" )
	private String oderSn;

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "memberId", getMemberId() )
				.append( "platformId", getPlatformId() )
				.append( "status", getStatus() )
				.append( "money", getMoney() )
				.append( "ctime", getCtime() )
				.append( "oderSn", getOderSn() )
				.toString();
	}
}