package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * member_bcode
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
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

	private String[] selectDate;
	private String   startTime;
	private String   endTime;

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