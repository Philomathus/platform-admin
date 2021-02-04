package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateRsp extends ImRsp {
    @JsonProperty("GroupId")
    protected String groupId;
}
