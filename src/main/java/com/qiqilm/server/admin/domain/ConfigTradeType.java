package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 资金交易类型对象 config_trade_type
 *
 * @author 77tv
 * @date 2021-01-29
 */
public class ConfigTradeType extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 交易类型
	 */
	@Excel( name = "交易类型" )
	private Long type;

	/**
	 * 交易名称
	 */
	@Excel( name = "交易名称" )
	private String name;

	/**
	 * 交易说明
	 */
	@Excel( name = "交易说明" )
	private String des;

	public Long getType() {
		return type;
	}

	public void setType( Long type ) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getDes() {
		return des;
	}

	public void setDes( String des ) {
		this.des = des;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "type", getType() )
				.append( "name", getName() )
				.append( "des", getDes() )
				.toString();
	}
}