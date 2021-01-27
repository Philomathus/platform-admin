package com.qiqilm.server.admin.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by admin on 18/4/12.
 */
@Data
public class PageBO<T> {
    @ApiModelProperty(value = "分页数据")
    Integer code = 0;
    @ApiModelProperty(value = "分页数据")
    List<T> data;
    @ApiModelProperty(value = "总记录数")
    Long count;
}
