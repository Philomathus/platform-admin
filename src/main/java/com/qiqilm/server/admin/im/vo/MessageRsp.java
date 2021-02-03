package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageRsp  extends ImRsp {
    @JsonProperty("MsgTime")
    private Integer msgTime;
    @JsonProperty("MsgKey")
    private String msgKey;
}
