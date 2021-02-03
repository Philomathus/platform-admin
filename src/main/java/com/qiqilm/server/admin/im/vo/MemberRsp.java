package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author kehai
 * @Date 2020/7/10 10:44
 * @Version 1.0
 */
@Data
public class MemberRsp {
    @JsonProperty("JoinTime")
    private Integer joinTime;
    @JsonProperty("LastSendMsgTime")
    private Integer lastSendMsgTime;
    @JsonProperty("Member_Account")
    private String memberAccount;
    @JsonProperty("MsgFlag")
    private String msgFlag;
    @JsonProperty("MsgSeq")
    private Integer msgSeq;
    @JsonProperty("NameCard")
    private String nameCard;
    @JsonProperty("Role")
    private String role;
    @JsonProperty("ShutUpUntil")
    private Integer shutUpUntil;
}
