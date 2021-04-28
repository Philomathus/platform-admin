package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author kehai
 * @Date 2020/8/3 15:22
 * @Version 1.0
 */
@Data
public class ForbidItem {
    @JsonProperty("Member_Account")
    private String account; // 用户 ID
    //    @JsonProperty("Member_Account")
    private String nickName; // 用户 ID
    @JsonProperty("ShuttedUntil")
    private String shuttedUnitl; // 禁言到的时间（使用 UTC 时间，即世界协调时间）

    private Integer shutTamp;
}
