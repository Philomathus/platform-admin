package com.qiqilm.server.admin.domain.rsp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RspGameMoney {
    @ApiModelProperty(value = "id" )
    private String id;
    @ApiModelProperty(value = "上分金额" )
    private BigDecimal money;
    @ApiModelProperty(value = "本地平台id")
    private Integer platform_id;
    @ApiModelProperty(value = "本地平台名称")
    private String platform_name;
}
