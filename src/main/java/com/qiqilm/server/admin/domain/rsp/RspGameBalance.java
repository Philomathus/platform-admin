package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RspGameBalance {
	private String     name;
	private BigDecimal value;
	private long       type;
}
