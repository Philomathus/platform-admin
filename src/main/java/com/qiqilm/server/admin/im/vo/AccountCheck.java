package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountCheck {
    @JsonProperty("UserID")
    private String userID;
    @JsonProperty("ResultCode")
    private Integer resultCode;
    @JsonProperty("ResultInfo")
    private String resultInfo;
    @JsonProperty("AccountStatus")
    private String accountStatus;
}
