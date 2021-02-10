package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 线上充值信息对象 member_pay_jour
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class MemberPayJour extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统编号
	 */
	private String id;

	@Excel( name = "会员id", sort = 1 )
	private String memberId;

	/**
	 * 支付平台编号
	 */
	private String platformId;

	/**
	 * 支付通道编码
	 */
	private String channelId;

	/**
	 * 支付方式
	 */
	private String paymentMethod;

	@Excel( name = "订单号", sort = 2 )
	private String orderNo;

	@Excel( name = "上游订单号", sort = 3 )
	private String tradeSn;

	@Excel( name = "请求金额", sort = 4 )
	private BigDecimal money;

	@Excel( name = "实际金额", sort = 5 )
	private BigDecimal subMoney;

	/**
	 * 支付接口的支付地址
	 */
	private String paymentCode;

	/**
	 * 支付成功时间(上游回调时间)
	 */
	private String paymentTime;

	@Excel( name = "商户下单时间", sort = 6 )
	private String payTime;

	@Excel( name = "订单状态", dictType = "pay_jour_status", sort = 8 )
	private String status;

	/**
	 * 是否是人工补单
	 */
	private Integer isPatchOrder;

	/**
	 * 通道手续费
	 */
	private BigDecimal platformRate;

	@Excel( name = "通道成功率", defaultValue = "0", scale = 0, multiply = 100,
			roundingMode = BigDecimal.ROUND_HALF_UP, suffix = "%", sort = 9 )
	private BigDecimal currentSuccessRate;

	/**
	 * 补单操作员
	 */
	private String manWork;

	/**
	 * 账号
	 */
	private String userName;

	/**
	 * 是否首次1是0否
	 */
	private Long first;

	private String createTimes;

	@Excel( name = "回调时间", sort = 7 )
	private String updateTimes;

	@Excel( name = "备注", sort = 13 )
	private String remark;

	@Excel( name = "支付通道费率", defaultValue = "0", scale = 0, multiply = 100,
			roundingMode = BigDecimal.ROUND_HALF_UP, suffix = "%", sort = 10 )
	private BigDecimal payRate;

	@Excel( name = "支付平台名称", sort = 11 )
	private String platformName;

	@Excel( name = "支付通道名称", sort = 12 )
	private String channelName;

	private String currentSuccessRateStr;
	private String payRateStr;

	@JsonIgnore
	private String   searchOrderNo;
	@JsonIgnore
	private String[] selectDate;
	@JsonIgnore
	private String   selectStartDate;
	@JsonIgnore
	private String   selectEndDate;

	public String getCurrentSuccessRateStr() {
		if ( currentSuccessRate != null ) {
			return currentSuccessRate.multiply( new BigDecimal( 100 ) ).setScale( 0, RoundingMode.HALF_UP ).toString().concat(
					"%" );
		}
		return "";
	}

	public String getPayRateStr() {
		if ( payRate != null ) {
			return payRate.multiply( new BigDecimal( 100 ) ).setScale( 0, RoundingMode.HALF_UP ).toString().concat( "%" );
		}
		return "";
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "memberId", getMemberId() )
				.append( "platformId", getPlatformId() )
				.append( "channelId", getChannelId() )
				.append( "paymentMethod", getPaymentMethod() )
				.append( "orderNo", getOrderNo() )
				.append( "tradeSn", getTradeSn() )
				.append( "money", getMoney() )
				.append( "subMoney", getSubMoney() )
				.append( "paymentCode", getPaymentCode() )
				.append( "paymentTime", getPaymentTime() )
				.append( "payTime", getPayTime() )
				.append( "status", getStatus() )
				.append( "isPatchOrder", getIsPatchOrder() )
				.append( "remark", getRemark() )
				.append( "platformRate", getPlatformRate() )
				.append( "currentSuccessRate", getCurrentSuccessRate() )
				.append( "manWork", getManWork() )
				.append( "userName", getUserName() )
				.append( "createTime", getCreateTime() )
				.append( "updateTime", getUpdateTime() )
				.append( "first", getFirst() )

				.append( "payRate", getPayRate() )
				.append( "platformName", getPlatformName() )
				.append( "channelName", getChannelName() )
				.toString();
	}
}
