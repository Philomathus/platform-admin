package com.qiqilm.server.admin.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberSumWithdraw {
    private String     memberId;
    private BigDecimal money;
    private String     bankCode;
}
