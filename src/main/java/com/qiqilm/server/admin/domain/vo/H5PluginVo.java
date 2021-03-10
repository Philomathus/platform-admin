package com.qiqilm.server.admin.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel()
public class H5PluginVo {
    @ApiModelProperty(value = "1=彩票2=转盘彩票3=转盘坐骑")
    private Integer type;
    @ApiModelProperty(value = "图标地址")
    private String icon;
    @ApiModelProperty(value = "访问地址")
    private String link;
    @ApiModelProperty(value = "1 启用 0 禁用")
    private boolean status;

    @ApiModelProperty(value = "彩票ID（彩票专用）")
    private Integer id;
    @ApiModelProperty(value = "结束倒计时（彩票专用）")
    private Long cutDown ;
    @ApiModelProperty(value = "封盘时间（彩票专用）")
    private long fDown ;
    @ApiModelProperty(value = "彩票类型")
    private String lotteryType;
    @ApiModelProperty(value = "彩票名")
    private String lotteryName;


}
