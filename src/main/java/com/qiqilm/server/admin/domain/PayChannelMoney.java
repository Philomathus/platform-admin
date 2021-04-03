package com.qiqilm.server.admin.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 支付通道金额对象 pay_channel_money
 *
 * @author 77tv
 * @date 2021-04-03
 */
@Data
public class PayChannelMoney {
	private static final long serialVersionUID = 1L;

	/** 主键 */
	private Long id;

	/** 通道金额 */
	private Long money;

	/** 通道ID */
	private Long channelId;

	/** 通道费率 */
	private BigDecimal channelPayRate;

	/** 支付类型CODE */
	private Integer typeCode;

	/** 最低层级 */
	private Long openLevelMin;

	/** 最高层级 */
	private Long openLevelMax;

	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
				.append("id", getId())
				.append("money", getMoney())
				.append("channelId", getChannelId())
				.append("channelPayRate", getChannelPayRate())
				.append("typeCode", getTypeCode())
				.append("openLevelMin", getOpenLevelMin())
				.append("openLevelMax", getOpenLevelMax())
				.toString();
	}
}