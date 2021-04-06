package com.qiqilm.server.admin.domain.req;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReqPayAgent {
	private Long         payAgentPlatId;
	private String       withdrawOrderNo;
	private List<String> withdrawOrderNos;
	private Integer      googleAuthCode;

	// 失败原因
	private String failReason;

	private Date currentTime;
}

