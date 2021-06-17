package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 线上充值信息对象 member_pay_jour
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class RspActivityCashBack {

	@Excel( name = "会员编号", orderNum = "1" )
	private String user_id;

	@Excel( name = "备注信息", orderNum = "4" )
	private String mark;

	@Excel( name = "金额", orderNum = "2" )
	private BigDecimal income;

	@Excel( name = "返现时间", orderNum = "3" )
	private String create_time;
}
