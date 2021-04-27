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
public class UserForbidList implements BaseFuc {
    @JsonProperty("userId")
    private String userId;

    @Override
    public String getApi() {
        return "/openconfigsvr/getnospeaking";
    }

    @Override
    public ObjectNode get() {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode node = mapper.createObjectNode();
        node.put("Get_Account",userId);
        return node;
    }
}
