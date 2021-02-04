package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class StatusAccount implements BaseFuc {
    private boolean needDetail;
    private List<String> To_Account;

    @Override
    public String getApi() {
        return "/openim/querystate";
    }

    @Override
    public ObjectNode get() {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        final ArrayNode array = mapper.createArrayNode();
        if(Objects.nonNull(To_Account) && !To_Account.isEmpty()){
            for (String account : To_Account) {
                array.add(account);
            }
        }

        objectNode.put("To_Account",array);
        if(Objects.nonNull(needDetail) && needDetail){
            objectNode.put("IsNeedDetail",1);
        }

        return objectNode;
    }
}
