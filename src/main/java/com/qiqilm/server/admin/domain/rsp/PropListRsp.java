package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropListRsp {
	private Integer id;

	private String name;

	private String icon;

	private BigDecimal diamonds;

	private Integer isMuch;

}
