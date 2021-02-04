package com.qiqilm.server.admin.domain.req;

import lombok.Data;

@Data
public class ReqPayJour {
	private String   id;
	private Integer  status;
	private String   searchValue;
	private String   platformId;
	private String   searchOrderNo;
	private String[] selectDate;
	private String   selectStartDate;
	private String   selectEndDate;
}
