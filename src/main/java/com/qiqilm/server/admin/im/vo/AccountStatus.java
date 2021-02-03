package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AccountStatus {
    @JsonProperty("To_Account")
    private String toAccount;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Detail")
    private List<AccountStatusDetail> detail;
}
