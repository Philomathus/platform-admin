package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class RspPayJour  {
	/**
	 * 支付平台编号
	 */
	private String platformId;

	/**
	 * 支付通道编码
	 */
	private String channelId;

	@Excel( name = "请求金额", orderNum = "1" )
	private BigDecimal money;

	@Excel( name = "实际金额", orderNum = "2" )
	private BigDecimal subMoney;

	@Excel( name = "支付平台名称", orderNum = "3" )
	private String platformName;

	@Excel( name = "支付通道名称", orderNum = "4" )
	private String channelName;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Excel(name = "回调时间",  exportFormat = "yyyy-MM-dd")
	private String updateTime;



}
