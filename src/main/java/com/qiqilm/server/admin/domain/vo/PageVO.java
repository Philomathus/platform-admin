package com.qiqilm.server.admin.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by admin on 18/4/12.
 */
@Data
public class PageVO {

    @ApiModelProperty(value = "当前分页数",required = true)
    private Integer page;
    @ApiModelProperty(value = "分页大小",required = true)
    private  Integer limit;
    public static PageVO ofPage(int page,int limit){
        PageVO vo = new PageVO();
        vo.setPage(page);
        vo.setLimit(limit);
        return vo;
    }

    public Integer getParam1(){
        return page * limit - limit;
    }

    public Integer getParam2(){
        return limit;
    }
}
