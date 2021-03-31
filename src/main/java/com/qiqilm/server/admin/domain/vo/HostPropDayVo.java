package com.qiqilm.server.admin.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HostPropDayVo {
    private Integer hostId;
    private BigDecimal sumHostProp;
}
