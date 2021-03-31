package com.qiqilm.server.admin.domain.rsp;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class RspTestAccountProp {
	@Excel( name = "会员id", sort = 1 )
	private String  pUserId;

	@Excel( name = "会员昵称", sort = 2)
	private String pUserName;

	@Excel( name = "送礼金额", sort = 3 )
	private BigDecimal totalDiamonds;

	@Excel( name = "主播id", sort = 4 )
	private String toUserId;

	private BigDecimal testAccountPorpTotal;





}