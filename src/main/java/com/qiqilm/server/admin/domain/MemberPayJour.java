package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 线上充值信息对象 member_pay_jour
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class MemberPayJour implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统编号
	 */
	private String id;

	@Excel( name = "会员id", orderNum = "0" )
	private String memberId;

	/**
	 * 支付平台编号
	 */
	private String platformId;

	/**
	 * 支付通道编码
	 */
	private String channelId;

	@Excel( name = "订单号", orderNum = "1" )
	private String orderNo;

	@Excel( name = "上游订单号", orderNum = "3" )
	private String tradeSn;

	@Excel( name = "请求金额", orderNum = "2" )
	private BigDecimal money;

	@Excel( name = "实际金额", orderNum = "7" )
	private BigDecimal subMoney;

	/**
	 * 支付接口的支付地址
	 */
	private String paymentCode;

	@Excel( name = "商户下单时间", orderNum = "6" )
	private String payTime;

	private String status;

	/**
	 * 是否是人工补单
	 */
	private Integer isPatchOrder;

	/**
	 * 通道手续费
	 */
	private BigDecimal platformRate;

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

	private String createTime;

	@Excel( name = "回调时间", orderNum = "10" )
	private String updateTime;

	@Excel( name = "备注", orderNum = "9" )
	private String remark;

	private BigDecimal payRate;

	@Excel( name = "支付平台名称", orderNum = "4" )
	private String platformName;

	@Excel( name = "支付通道名称", orderNum = "5" )
	private String channelName;

	@Excel( name = "通道成功率", orderNum = "11" )
	private String currentSuccessRateStr;

	@Excel( name = "支付通道费率", orderNum = "12" )
	private String payRateStr;

	@Excel( name = "订单状态", orderNum = "8" )
	private String statusDes;

	@JsonIgnore
	private String   searchValue;
	@JsonIgnore
	private String   searchOrderNo;
	@JsonIgnore
	private String[] selectDate;
	@JsonIgnore
	private String   selectStartDate;
	@JsonIgnore
	private String   selectEndDate;
	@JsonIgnore
	private List<String> channelIds;

	public String getCurrentSuccessRateStr() {
		if ( currentSuccessRate != null ) {
			return currentSuccessRate.multiply( new BigDecimal( 100 ) ).setScale( 0, RoundingMode.HALF_UP ).toString().concat(
					"%" );
		}
		return "";
	}

	public String getPayRateStr() {
		if ( payRate != null ) {
			String payRateStr = payRate.multiply( new BigDecimal( 100 ) ).setScale( 1, RoundingMode.HALF_UP ).toString();
			if ( payRateStr.endsWith( "0" ) ) {
				payRateStr = payRate.multiply( new BigDecimal( 100 ) ).setScale( 0, RoundingMode.HALF_UP ).toString();
			}
			return payRateStr.concat( "%" );
		}
		return "";
	}

	public String getStatusDes() {
		if ( StringUtils.hasText( status ) ) {
			switch ( status ) {
			case "1":
				return "成功";
			case "0":
				return "失败";
			case "-1":
				return "待确认";
			}
		}
		return "待确认";
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "memberId", getMemberId() )
				.append( "platformId", getPlatformId() )
				.append( "channelId", getChannelId() )
				.append( "orderNo", getOrderNo() )
				.append( "tradeSn", getTradeSn() )
				.append( "money", getMoney() )
				.append( "subMoney", getSubMoney() )
				.append( "paymentCode", getPaymentCode() )
				.append( "payTime", getPayTime() )
				.append( "status", getStatus() )
				.append( "isPatchOrder", getIsPatchOrder() )
				.append( "remark", getRemark() )
				.append( "platformRate", getPlatformRate() )
				.append( "currentSuccessRate", getCurrentSuccessRate() )
				.append( "manWork", getManWork() )
				.append( "userName", getUserName() )
				.append( "first", getFirst() )

				.append( "payRate", getPayRate() )
				.append( "platformName", getPlatformName() )
				.append( "channelName", getChannelName() )
				.toString();
	}
}
