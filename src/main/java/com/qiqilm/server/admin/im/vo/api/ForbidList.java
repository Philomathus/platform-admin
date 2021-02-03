package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

/**
 * @Author kehai
 * @Date 2020/8/3 15:24
 * @Version 1.0
 */
@Data
public class ForbidList implements BaseFuc {
    @JsonProperty("GroupId")
    private String GroupId;

    @Override
    public String getApi() {
        return "/group_open_http_svc/get_group_shutted_uin";
    }

    @Override
    public ObjectNode get() {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode node = mapper.createObjectNode();
        node.put("GroupId",GroupId);
        return node;
    }
}
