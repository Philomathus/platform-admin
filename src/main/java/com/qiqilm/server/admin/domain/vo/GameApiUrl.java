package com.qiqilm.server.admin.domain.vo;

import lombok.Data;

@Data
public class GameApiUrl {
    private Integer code;

    private String url;

    private Integer status;

    private Float money ;
    private Float freeMoney ;

    private Integer gameStatus;
}
