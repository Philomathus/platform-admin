package com.qiqilm.server.admin.domain.vo;

import lombok.Data;

@Data
public class ReqMemberInfo extends PageVO {

    private String id;

    private String search;

    private String inviter_code;

    private Integer status;

    private Integer money;

    private Integer channelCode;

    private String nick_name;

    private String login_ip;

    private String password;

    private String user_name;

    private String orderBy;

}
