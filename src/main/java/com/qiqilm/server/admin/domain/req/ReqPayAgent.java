package com.qiqilm.server.admin.domain.req;

import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ReqPayAgent {
	private Long payAgentPlatId;
	private String  withdrawOrderNo;
	private Integer googleAuthCode;

	// 失败原因
	@Transient
	private String failReason;

	private Date currentTime;
}
