package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

@Data
public class RspMemberInfo {

    private String email;
    private String id;

    private String user_name;
    private String phone;

    private String vip;
    private String reg_time;
    private String total_account;
    private String code_account;
    private String code_total;
    private String login_ip;
    private String ipaddress;
    //线下充值金额
    private String rechargemoney;
    //线上金额(一月)
    private String submoney;
    //人工代充金额
    private String p_money;
    //手动增加金额
    private String rg_income;
    //平台赠送金额
    private String zs_income;
    //充值总的金额
    private String totalincom;
    //会员提现次数
    private String w_count;
    //会员提现金额
    private String w_sum;
    //彩票异常投注次数
    private String gcount;
    //彩票总投注笔数
    private String gtcount;

    //游戏名称
    private String class_twoname;
    //游戏投注
    private String touZhu;
    //游戏投注盈利
    private String yingLi;
}
