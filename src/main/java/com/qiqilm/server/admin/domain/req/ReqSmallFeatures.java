package com.qiqilm.server.admin.domain.req;

import lombok.Data;

@Data
public class ReqSmallFeatures {
    private String phones;
    private String password;
    private String userIds;
    private String phonesByIds;
    private String memberIds;
    private String money;
    private String beat;
    private Integer googleAuthCode;
}
