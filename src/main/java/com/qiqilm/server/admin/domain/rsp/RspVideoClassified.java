package com.qiqilm.server.admin.domain.rsp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RspVideoClassified {
    @ApiModelProperty(value = "分类名称")
    private String title;
    @ApiModelProperty(value = "分类id")
    private Integer classfy;
}
