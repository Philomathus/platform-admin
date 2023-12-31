package com.qiqilm.server.admin.domain.rsp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RspReportMemberStatistics {
    private String member_id;
    private BigDecimal total_recharge;
    private BigDecimal total_withdrawal;
    private BigDecimal total_account;
    private Integer total_registration;
}
