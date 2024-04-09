package com.qiqilm.server.admin.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ActivityWithdrawCashBack {
    private String bankCode;
    private BigDecimal withdrawTotalMin;
    private BigDecimal withdrawTotalMax;
    private Double rate;
    private int status;
}