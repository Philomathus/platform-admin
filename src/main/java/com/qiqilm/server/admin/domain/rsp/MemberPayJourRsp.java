package com.qiqilm.server.admin.domain.rsp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPayJourRsp {
    private String id;

    @ApiModelProperty(name = "会员id")
    private String memberId;

    @ApiModelProperty(name = "请求金额")
    private BigDecimal money;

    @ApiModelProperty(name = "支付平台名称")
    private String platformName;

    @ApiModelProperty(name = "支付通道名称")
    private String channelName;

    @ApiModelProperty("订单状态 (1成功 0失败 -1待确认)")
    private String status;

    @ApiModelProperty(name = "订单时间")
    private String orderTime;

    private String platformId;

    private String channelId;
}
