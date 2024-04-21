package com.qiqilm.server.admin.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActivityWithdrawCashBack {
    private Integer    id;
    private String     bankCode;
    private Long       withdrawTotalMin;
    private Long       withdrawTotalMax;
    private BigDecimal rate;
    private BigDecimal bcodeRate;
    private Integer    status;
}