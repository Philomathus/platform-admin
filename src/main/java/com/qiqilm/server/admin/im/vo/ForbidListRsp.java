package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author kehai
 * @Date 2020/8/3 15:21
 * @Version 1.0
 */
@Data
public class ForbidListRsp extends ImRsp {
    @JsonProperty("ShuttedUinList")
    List<ForbidItem> shuttedUin;
}
