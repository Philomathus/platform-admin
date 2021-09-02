package com.qiqilm.server.admin.domain.req;

import lombok.Data;

import java.util.List;

@Data
public class ReqMemberWithdrawLog {
	private String       id;
	private List<String> ids;
	private String       remark;
	private String       orderNo;
	private Long         payAgentPlatId;
}
