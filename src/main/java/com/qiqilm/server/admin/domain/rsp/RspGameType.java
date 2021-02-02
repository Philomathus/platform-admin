package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

@Data
public class RspGameType {

    private String id;

    private String name;

    private Integer status;

    private Integer indexs;

    private String icon;

    private Integer game_type;

    private Integer icon_type;
}
