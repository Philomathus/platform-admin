package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author kehai
 * @Date 2020/7/10 10:42
 * @Version 1.0
 */
@Data
public class GroupMemberListRsp extends ImRsp {
    @JsonProperty("MemberNum")
    private Integer memberNum;
    @JsonProperty("MemberList")
    private List<MemberRsp> memberList;
}
