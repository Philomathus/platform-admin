package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 资金交易类型对象 config_trade_type
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Data
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



	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "type", getType() )
				.append( "name", getName() )
				.append( "des", getDes() )
				.toString();
	}
}