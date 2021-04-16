package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员上下分对象 log_game_order
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Data
public class LogGameOrder extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 本地ID
	 */
	private String id;

	/**
	 * 1上分2下分
	 */
	@Excel( name = "1上分2下分" )
	private Integer type;

	/**
	 * 玩家ID
	 */
	@Excel( name = "玩家ID" )
	private String memberId;

	/**
	 * 账号
	 */
	@Excel( name = "账号" )
	private String userName;

	/**
	 * 本地平台id
	 */
	private Integer platformId;

	@Excel( name = "游戏平台" )
	private String platformName;

	/**
	 * 0开始1失败2成功3异常
	 */
	@Excel( name = "0开始1失败2成功3异常" )
	private Integer status;

	/**
	 * 金额
	 */
	@Excel( name = "金额" )
	private BigDecimal money;

	/**
	 * 开始时间
	 */
	@JsonFormat( pattern = DateFormatUtils.SPLIT_PATTERN_DATETIME )
	@Excel( name = "开始时间", width = 30, dateFormat = DateFormatUtils.SPLIT_PATTERN_DATETIME )
	private Date bTime;

	/**
	 * 结束时间
	 */
	@JsonFormat( pattern = DateFormatUtils.SPLIT_PATTERN_DATETIME )
	@Excel( name = "结束时间", width = 30, dateFormat = DateFormatUtils.SPLIT_PATTERN_DATETIME )
	private Date     eTime;
	/**
	 * 选择日期
	 */
	private String[] selectDate;
	private String   startTime;
	private String   endTime;

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "type", getType() )
				.append( "memberId", getMemberId() )
				.append( "userName", getUserName() )
				.append( "platformId", getPlatformId() )
				.append( "status", getStatus() )
				.append( "money", getMoney() )
				.append( "bTime", getBTime() )
				.append( "eTime", getETime() )
				.toString();
	}
}
