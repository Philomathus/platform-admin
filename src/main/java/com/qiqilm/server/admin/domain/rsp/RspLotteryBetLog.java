package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

@Data
public class RspLotteryBetLog {
    private String id;
    private String userid;

    private String son_platform_name;

    private String issue;
    private String per_price;
    private String prize;//奖金
    private String game_name;
    private String bet_time;
    private String bet_select;//彩票内容
    private String code;//彩票码
    private String bet_amount;//彩票金额

}
