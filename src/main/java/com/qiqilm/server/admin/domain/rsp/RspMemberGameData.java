package com.qiqilm.server.admin.domain.rsp;

import com.qiqilm.server.admin.annotation.Excel;
import lombok.Data;

@Data
public class RspMemberGameData {

    @Excel(name = "账号")
    private String account;

    private String kind_id;
    private String game_id;

    @Excel(name = "有效下注")
    private String cell_score;

    @Excel(name = "总下注")
    private String all_bet;

    @Excel(name = "盈利")
    private String profit;

    @Excel(name = "抽水")
    private String revenue;

    @Excel(name = "游戏开始时间")
    private String game_start_time;

    @Excel(name = "游戏结束时间")
    private String game_end_time;

    private Integer platform_id;

    @Excel(name = "平台ID")
    private String agent;

    private String platform_type;

    private Integer status;


    @Excel(name = "子平台名称")
    private String sonPlatformName;

    @Excel(name = "平台名称")
    private String platformName;
}
