package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AccountStatusRsp extends ImRsp {
    @JsonProperty("QueryResult")
    private List<AccountStatus>  queryResult;
    @JsonProperty("ErrorList")
    private List<AccountStatusError> errorList;
}
