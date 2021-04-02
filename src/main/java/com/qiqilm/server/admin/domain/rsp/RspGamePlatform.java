package com.qiqilm.server.admin.domain.rsp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RspGamePlatform {

    @ApiModelProperty(value = "平台ID")
    private Integer id;
    @ApiModelProperty(value = "代理（渠道）号")
    private String agent;
    @ApiModelProperty(value = "平台名称")
    private String name;
    @ApiModelProperty(value = "游戏类型")
    private String game_typeID;
    @ApiModelProperty(value = "洗码比例")
    private BigDecimal rate_clean;

}
