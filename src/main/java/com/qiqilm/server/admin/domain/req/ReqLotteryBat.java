package com.qiqilm.server.admin.domain.req;

import lombok.Data;

@Data
public class ReqLotteryBat {
	private Integer  id;
	private String   nickName;
	private String[] selectDate;
	private String   startTime;
	private String   endTime;
}
