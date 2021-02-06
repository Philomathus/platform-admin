package com.qiqilm.server.admin.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Accessors( chain = true )
@Data
public class ReqLiveHostWageNote {
	private Integer             id;
	private Integer             family_id;
	private Integer             host_id;
	private Integer             live_time_sec;
	private BigDecimal          ticket;
	private String              create_time;
	private String              nick_name;
	private String              startTime;
	private String              endTime;
	//结算汇率
	private BigDecimal SettlementRate;
	//家族名称
	private String              familyName;
	//族长昵称
	private String              familyNickName;

	private Integer              userfamily_id;


}
