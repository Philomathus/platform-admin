package com.qiqilm.server.admin.domain.vo;

import lombok.Data;

/**
 * Created by admin on 18/4/12.
 */
@Data
public class PageVO {

    private Integer page;

    private  Integer limit;
    public static PageVO ofPage(int page,int limit){
        PageVO vo = new PageVO();
        vo.setPage(page);
        vo.setLimit(limit);
        return vo;
    }

}
