package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.Objects;

/**
 * @Author kehai
 * @Date 2020/7/9 14:56
 * @Version 1.0
 */
@Data
public class MessageHistory implements BaseFuc {

    private String groupId;    //拉取消息的群 ID
    private Integer reqMsgNumber = 10;      //需要拉取的消息条数
    private Integer reqMsgSeq;

    @Override
    public String getApi() {
        return "/group_open_http_svc/group_msg_get_simple";
    }

    @Override
    public ObjectNode get() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("GroupId",groupId);
        node.put("ReqMsgNumber",reqMsgNumber);
        if(Objects.nonNull(reqMsgSeq))
            node.put("ReqMsgSeq",reqMsgSeq);
        return node;
    }
}
