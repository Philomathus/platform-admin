package com.qiqilm.server.admin.domain.rsp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RspVipSet {
    @ApiModelProperty( value = "vip等级" )
    private Integer    level_flag;
    @ApiModelProperty( value = "晋级彩金" )
    private BigDecimal jjcj;
    @ApiModelProperty( value = "周俸禄)" )
    private BigDecimal zfl;
    @ApiModelProperty( value = "月俸禄" )
    private BigDecimal yfl;
    @ApiModelProperty( value = "打码量" )
    private BigDecimal level_money;

    private BigDecimal weekCharge;
    private BigDecimal monthCharge;
    private BigDecimal bcodeMultiple;
}
