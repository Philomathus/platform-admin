package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author kehai
 * @Date 2020/7/9 15:22
 * @Version 1.0
 */
@Data
public class RspMsgBody {
    @JsonProperty("MsgType")
    private String msgType;
    @JsonProperty("MsgContent")
    private MsgContent msgContent;
}
