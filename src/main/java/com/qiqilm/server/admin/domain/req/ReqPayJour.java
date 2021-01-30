package com.qiqilm.server.admin.domain.req;

import lombok.Data;

@Data
public class ReqPayJour {
	private String  id;
	private Integer status;
	private String  search;
	private String  platform_id;
	private String  trade_sn;
    private String[] selectDate;
    private String selectStartDate;
    private String selectEndDate;
    private String startDate;
	private String endDate;


}
