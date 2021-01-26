package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * member_bcode
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class MemberBcode extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统编号
	 */
	private String id;

	/**
	 * 会员账号ID
	 */
	@Excel( name = "会员账号ID" )
	private String userId;

	/**
	 * 描述
	 */
	@Excel( name = "描述" )
	private String des;

	/**
	 * 收入
	 */
	@Excel( name = "收入" )
	private BigDecimal income;

	/**
	 * 0=未打码 1=已打码
	 */
	@Excel( name = "0=未打码 1=已打码" )
	private Integer status;

	/**
	 * 当前打码量
	 */
	@Excel( name = "当前打码量" )
	private BigDecimal cur;

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId( String userId ) {
		this.userId = userId;
	}

	public String getDes() {
		return des;
	}

	public void setDes( String des ) {
		this.des = des;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome( BigDecimal income ) {
		this.income = income;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus( Integer status ) {
		this.status = status;
	}

	public BigDecimal getCur() {
		return cur;
	}

	public void setCur( BigDecimal cur ) {
		this.cur = cur;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "userId", getUserId() )
				.append( "des", getDes() )
				.append( "income", getIncome() )
				.append( "createTime", getCreateTime() )
				.append( "status", getStatus() )
				.append( "cur", getCur() )
				.toString();
	}
}