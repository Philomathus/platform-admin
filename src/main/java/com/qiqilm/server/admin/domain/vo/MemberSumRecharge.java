package com.qiqilm.server.admin.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberSumRecharge {
    private String     memberId;
    private BigDecimal money;
}
