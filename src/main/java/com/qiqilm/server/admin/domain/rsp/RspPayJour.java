package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

	@Excel(name = "通道费率",orderNum = "5")
	private BigDecimal channelPayRate;

	@Excel(name = "手续费",orderNum = "5")
	private BigDecimal handlingfee;

	@Excel(name = "结算金额",orderNum = "7")
	private BigDecimal remaining;

	public BigDecimal getHandlingfee() {
		if (channelPayRate==null){
			return BigDecimal.ZERO;
		}
		if (channelPayRate.compareTo(BigDecimal.ZERO)> 0 ){
			if (subMoney==null){
				subMoney=money;
			}
			return subMoney.multiply(channelPayRate).setScale( 2,
					RoundingMode.HALF_UP );
		}
		return BigDecimal.ZERO;
	}

	public BigDecimal getRemaining() {
		if (channelPayRate==null){
			return subMoney;
		}
		if (channelPayRate.compareTo(BigDecimal.ZERO)> 0 ){
			if (subMoney==null){
				subMoney=money;
			}
			return subMoney.subtract(subMoney.multiply(channelPayRate)).setScale( 2,
					RoundingMode.HALF_UP );
		}
		return subMoney;
	}
}
