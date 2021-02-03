package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountStatusDetail {
    @JsonProperty("Platform")
    private  String platform;
    @JsonProperty("Status")
    private String status;
}
