package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class DeleteAccount implements BaseFuc {
    private List<String> DeleteItem;

    @Override
    public String getApi() {
        return "/im_open_login_svc/account_delete";
    }

    @Override
    public ObjectNode get() {
        final ObjectMapper mapper = new ObjectMapper();
        final ArrayNode arrayNode = mapper.createArrayNode();
        final ObjectNode node = mapper.createObjectNode();

        if(Objects.nonNull(DeleteItem) && !DeleteItem.isEmpty()){
            for (String item : DeleteItem) {
                final ObjectNode itemNode = mapper.createObjectNode();
                itemNode.put("UserID",item);
                arrayNode.add(item);
            }
        }

        node.put("DeleteItem",arrayNode);
        return node;
    }
}
