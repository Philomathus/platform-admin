package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author kehai
 * @Date 2020/7/9 15:23
 * @Version 1.0
 */
@Data
public class MsgContent {
    @JsonProperty("Data")
    private String data;
    @JsonProperty("Desc")
    private String desc;
    @JsonProperty("Ext")
    private String ext;
}
