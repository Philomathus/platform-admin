package com.qiqilm.server.admin.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReqAddScore {
    private String id;
    private BigDecimal score = BigDecimal.ZERO;
    private String mk;
    private BigDecimal beatNum;
    private String ordermk;
    private Integer googleAuthCode;
    private String password;
}
