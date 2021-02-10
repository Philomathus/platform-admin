package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 会员资金信息对象 log_money
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Data
public class LogMoney extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统编号
	 */
	private String id;

	/**
	 * 会员编号
	 */
	@Excel( name = "会员编号" )
	private String userId;

	/**
	 * 账号
	 */
	@Excel( name = "账号" )
	private String userName;

	/**
	 * 行为类型
	 */
	private Integer type;

	/**
	 * 行为类型
	 */
	@Excel( name = "行为类型" )
	private String des;

	/**
	 * 收入
	 */
	@Excel( name = "收入" )
	private BigDecimal income;

	/**
	 * 支出
	 */
	@Excel( name = "支出" )
	private BigDecimal pay;

	/**
	 * 余额
	 */
	@Excel( name = "余额" )
	private BigDecimal total;

	/**
	 * 变化前余额
	 */
	@Excel( name = "变化前余额" )
	private BigDecimal totalBefore;

	/**
	 * 备注
	 */
	@Excel( name = "备注" )
	private String mark;

	/**
	 * 订单号备注
	 */
	@Excel( name = "订单号备注" )
	private String markorder;

	@JsonIgnore
	private String[] types;
	private String[] selectDate;
	@JsonIgnore
	private String   startTime;
	@JsonIgnore
	private String   endTime;

	public String getStartTime() {
		if ( StringUtils.isNotNull( this.getCreateTime() ) ) {
			return DateFormatUtils.formate( getCreateTime() );
		}
		return startTime;
	}

	public String getEndTime() {
		if ( StringUtils.isNotNull( this.getCreateTime() ) ) {
			return DateFormatUtils.formate( getCreateTime(), DateFormatUtils.SPLIT_PATTERN_DATE ) + " 23:59:59";
		}
		return endTime;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "userId", getUserId() )
				.append( "userName", getUserName() )
				.append( "type", getType() )
				.append( "des", getDes() )
				.append( "income", getIncome() )
				.append( "pay", getPay() )
				.append( "total", getTotal() )
				.append( "createTime", getCreateTime() )
				.append( "totalBefore", getTotalBefore() )
				.append( "mark", getMark() )
				.append( "markorder", getMarkorder() )
				.toString();
	}
}