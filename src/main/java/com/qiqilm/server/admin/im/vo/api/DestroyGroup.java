package com.qiqilm.server.admin.im.vo.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

@Data
public class DestroyGroup implements BaseFuc {

    private String GroupId;

    @Override
    public String getApi() {
        return "/group_open_http_svc/destroy_group";
    }

    @Override
    public ObjectNode get() {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode node = mapper.createObjectNode();
        node.put("GroupId",GroupId);
        return node;
    }
}
