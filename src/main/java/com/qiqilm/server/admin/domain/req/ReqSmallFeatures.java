package com.qiqilm.server.admin.domain.req;

import lombok.Data;

@Data
public class ReqSmallFeatures {
    private String phones;
    private String password;
    private Integer googleAuthCode;
}
