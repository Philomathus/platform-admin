package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author kehai
 * @Date 2020/7/7 8:36
 * @Version 1.0
 */
@Data
public class MsgRsp extends ImRsp {

    @JsonProperty("MsgSeq")
    private String MsgSeq;
}
