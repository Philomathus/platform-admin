package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MessageHistoryRsp extends ImRsp {
    @JsonProperty("GroupId")
    private String groupId;
    @JsonProperty("IsFinished")
    private Integer isFinished;
    @JsonProperty("RspMsgList")
    private List<RspMsg> rpsMsgList;
}
