package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 会员推广记录表对象 member_recommend
 *
 * @author 77tv
 * @date 2021-02-01
 */
public class MemberRecommend extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统编号
	 */
	private String id;

	/**
	 * 充值人ID
	 */
	@Excel( name = "充值人ID" )
	private String memberId;

	/**
	 * 邀请码
	 */
	@Excel( name = "邀请码" )
	private String code;

	/**
	 * 推广人ID
	 */
	@Excel( name = "推广人ID" )
	private String inviterId;

	/**
	 * 佣金
	 */
	@Excel( name = "佣金" )
	private BigDecimal commission;

	/**
	 * 状态
	 */
	@Excel( name = "状态" )
	private Integer status;

	/**
	 * 订单金额
	 */
	@Excel( name = "订单金额" )
	private BigDecimal orderMoney;

	/**
	 * 推广等级
	 */
	@Excel( name = "推广等级" )
	private Integer level;

	/**
	 * 充值人账号
	 */
	@Excel( name = "充值人账号" )
	private String memberName;

	/**
	 * 推广人账号
	 */
	@Excel( name = "推广人账号" )
	private String inviter;

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId( String memberId ) {
		this.memberId = memberId;
	}

	public String getCode() {
		return code;
	}

	public void setCode( String code ) {
		this.code = code;
	}

	public String getInviterId() {
		return inviterId;
	}

	public void setInviterId( String inviterId ) {
		this.inviterId = inviterId;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission( BigDecimal commission ) {
		this.commission = commission;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus( Integer status ) {
		this.status = status;
	}

	public BigDecimal getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney( BigDecimal orderMoney ) {
		this.orderMoney = orderMoney;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel( Integer level ) {
		this.level = level;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName( String memberName ) {
		this.memberName = memberName;
	}

	public String getInviter() {
		return inviter;
	}

	public void setInviter( String inviter ) {
		this.inviter = inviter;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "memberId", getMemberId() )
				.append( "code", getCode() )
				.append( "inviterId", getInviterId() )
				.append( "commission", getCommission() )
				.append( "createTime", getCreateTime() )
				.append( "status", getStatus() )
				.append( "orderMoney", getOrderMoney() )
				.append( "level", getLevel() )
				.append( "memberName", getMemberName() )
				.append( "inviter", getInviter() )
				.toString();
	}
}