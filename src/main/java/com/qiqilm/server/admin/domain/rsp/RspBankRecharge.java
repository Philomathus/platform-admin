package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 线上充值信息对象 member_pay_jour
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class RspBankRecharge {

	@Excel( name = "银行名称", orderNum = "1" )
	private String bankName;

	@Excel( name = "收款人", orderNum = "2" )
	private String bankUserName;

	@Excel( name = "充值金额", orderNum = "3" )
	private BigDecimal rechargeMoney;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Excel(name = "时间",  exportFormat = "yyyy-MM-dd")
	private String updateTime;

	private Integer time;



}
