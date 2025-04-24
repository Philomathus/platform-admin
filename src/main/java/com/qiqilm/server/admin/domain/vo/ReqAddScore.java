package com.qiqilm.server.admin.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReqAddScore {
    private String id;
    private BigDecimal score = BigDecimal.ZERO;
    private String mk;
    private String moneydes;
    private BigDecimal beatNum;
    private String remarkPay;
    private String ordermk;
    private Integer googleAuthCode;
    private String password;
    private String resetPasswordCode;
}
