package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountStatusError {
    @JsonProperty("To_Account")
    private String toAccount;
    @JsonProperty("ErrorCode")
    private Integer errorCode;
}
