package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author kehai
 * @Date 2020/7/9 15:22
 * @Version 1.0
 */
@Data
public class UserForbid extends ImRsp {
    @JsonProperty("ErrorCode")
    private Integer errorCode;
    @JsonProperty("ErrorInfo")
    private String errorInfo;
    @JsonProperty("C2CmsgNospeakingTime")
    private Integer c2CmsgNospeakingTime;
    @JsonProperty("GroupmsgNospeakingTime")
    private Integer groupmsgNospeakingTime;
}
