package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class RspTestAccountProp {
	@Excel( name = "会员id", orderNum = "1" )
	private String  pUserId;

	@Excel( name = "会员昵称", orderNum = "2" )
	private String pUserName;

	@Excel( name = "送礼金额", orderNum = "3" )
	private BigDecimal totalDiamonds;

	@Excel( name = "主播id", orderNum = "4" )
	private String toUserId;

	private BigDecimal testAccountPorpTotal;





}