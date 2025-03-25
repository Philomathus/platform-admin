package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RspVipSet {

    private Integer    level_flag;

    private BigDecimal jjcj;

    private BigDecimal zfl;

    private BigDecimal yfl;

    private BigDecimal level_money;

    private BigDecimal weekCharge;
    private BigDecimal monthCharge;
    private BigDecimal bcodeMultiple;
}
