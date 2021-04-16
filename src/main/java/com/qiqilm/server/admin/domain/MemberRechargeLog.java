package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员充值记录对象 member_recharge_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class MemberRechargeLog extends BaseEntity {
	private static final Long serialVersionUID = 1L;

	/** 系统编号 */
	private String id;

	/** 会员编号 */
	@Excel( name = "会员编号" )
	private String memberId;

	/**
	 * 创建时间
	 */
	@JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
	@Excel( name = "创建时间", isImportField = "true", exportFormat = "yyyy-MM-dd HH:mm:ss",
			importFormat = "yyyy-MM-dd HH:mm:ss", databaseFormat = "yyyy-MM-dd HH:mm:ss" )
	private Date createTime;

	/**
	 * 更新时间
	 */
	@JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
	@Excel( name = "更新时间", isImportField = "true", exportFormat = "yyyy-MM-dd HH:mm:ss",
			importFormat = "yyyy-MM-dd HH:mm:ss", databaseFormat = "yyyy-MM-dd HH:mm:ss" )
	private Date updateTime;

	/** 会员账号 */
	@Excel( name = "会员账号", orderNum = "1" )
	private String userName;

	/** 充值金额 */
	@Excel( name = "充值金额", orderNum = "2" )
	private BigDecimal rechargeMoney;

	/** 银行名称 */
	@Excel( name = "收款银行", orderNum = "5" )
	private String bankName;

	/** 银行账号 */
	@Excel( name = "收款账号", orderNum = "6" )
	private String bankAccount;

	private String[] selectDate;

	/** 状态(0已提交1初级审核通过2审核不通过3终极审核通过4入库失败) */
	private Integer status;

	@Excel( name = "订单状态", orderNum = "8" )
	private String statusDesc;

	/** 操作人 */
	@Excel( name = "操作人", orderNum = "9" )
	private String opName;

	/** 开户地址 */
	@Excel( name = "开户地址", orderNum = "10" )
	private String bankAddress;

	/** 充值类型(1线下，10线上) */
	private Integer type;

	/** 存款人姓名 */
	@Excel( name = "充值备注", orderNum = "7" )
	private String rechargeUserName;

	/** 收款人 */
	@Excel( name = "收款人", orderNum = "4" )
	private String bankUserName;

	/** 订单号 */
	@Excel( name = "订单号", orderNum = "3" )
	private String orderNo;

	/** 优惠比例 */
	private BigDecimal discountBill;

	/** 是否首次1是0否 */
	private Integer first;

	private String startDate;
	private String endDate;

	private String ip;

	public String getStatusDesc() {
		if ( status != null ) {
			switch ( status ) {
			case 0:
				return "未收款";
			case 1:
				return "初级审核通过";
			case 2:
				return "审核不通过";
			case 3:
				return "终极审核通过";
			case 4:
				return "入库失败";
			default:
			}
		}
		return "";
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "memberId", getMemberId() )
				.append( "userName", getUserName() )
				.append( "rechargeMoney", getRechargeMoney() )
				.append( "bankName", getBankName() )
				.append( "bankAccount", getBankAccount() )
				.append( "status", getStatus() )
				.append( "remark", getRemark() )
				.append( "opName", getOpName() )
				.append( "createTime", getCreateTime() )
				.append( "updateTime", getUpdateTime() )
				.append( "bankAddress", getBankAddress() )
				.append( "type", getType() )
				.append( "rechargeUserName", getRechargeUserName() )
				.append( "bankUserName", getBankUserName() )
				.append( "orderNo", getOrderNo() )
				.append( "discountBill", getDiscountBill() )
				.append( "first", getFirst() )
				.toString();
	}
}
