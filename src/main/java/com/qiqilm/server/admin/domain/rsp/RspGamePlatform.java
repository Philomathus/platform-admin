package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RspGamePlatform {

    private Integer id;

    private String agent;

    private String name;

    private String game_typeID;

    private BigDecimal rate_clean;

}
