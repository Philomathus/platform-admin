package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RspMemberOnline {
    private String id;

    private String member_code;

    private String invite_money;

    private String code_total;

    private String code_account;

    private String user_name;

    private String real_name;

    private Integer status;

    private Integer vip;

    private BigDecimal total_account;

    private Integer is_online;

    private Integer sex;

    private String phone;

    private String login_ip;

    private String login_address;

    private Integer login_dev;

    private String agent_id;

    private String agent_name;

    private Date reg_time;

    private String link_url;

    private Integer online_time;

    private String button_auth;

    private String cx_agent;

    private String inviter_code;

    private String channelCode;

    private BigDecimal box_account;

    private String nick_name;

    private Integer login_num;

    private Integer count;
}
