package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RspPlamGamesMonth {

    private String gameplame;

    private String yingli;

    private String date;
    private BigDecimal countBetMoney;


}
