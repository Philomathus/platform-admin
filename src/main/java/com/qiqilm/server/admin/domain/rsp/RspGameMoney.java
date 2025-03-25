package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RspGameMoney {

    private String id;

    private BigDecimal money;

    private Integer platform_id;

    private String platform_name;
}
