package com.qiqilm.server.admin.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberSumWithdraw {
    private String     memberId;
    private BigDecimal money;
    private String     bankCode;
}
