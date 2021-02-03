package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @Author kehai
 * @Date 2020/7/6 19:52
 * @Version 1.0
 */
@Data
public class SendSystemNotification implements BaseFuc {
    private String groupId;
    private List<String> members;  // 接收者群成员列表，不填或为空表示全员下发
    private String content;

    @Override
    public String getApi() {
        return "/group_open_http_svc/send_group_system_notification";
    }

    @Override
    public ObjectNode get() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("GroupId",groupId);
        node.put("Content",content);

        if(Objects.nonNull(members) && !members.isEmpty()){
            ArrayNode arrayNode = mapper.createArrayNode();
            for (String member : members) {
                arrayNode.add(member);
            }
            node.put("ToMembers_Account",arrayNode);
        }

        return node;
    }
}
