package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
public class SendOne implements BaseFuc {

    private Integer SyncOtherMachine = 2;
    private String  From_Account;
    private String To_Account;
    private Long MsgLifeTime = TimeUnit.DAYS.toSeconds(1);
    private Integer MsgRandom;
    private Long MsgTimeStamp;
    private List<ObjectNode> MsgBody;

    @Override
    public String getApi() {
        return "/openim/sendmsg";
    }

    @Override
    public ObjectNode get() {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode node = mapper.createObjectNode();
        MsgTimeStamp = System.currentTimeMillis()/1000;
        MsgRandom = (int)(Math.random()*999999);
        node.put("From_Account",From_Account);
        node.put("To_Account",To_Account);
        node.put("SyncOtherMachine",SyncOtherMachine);
        node.put("MsgLifeTime",MsgLifeTime);
        node.put("MsgRandom",MsgRandom);
        node.put("MsgTimeStamp",MsgTimeStamp);
        final ArrayNode array = mapper.createArrayNode();
        for (ObjectNode nodes : MsgBody) {
            array.add(nodes);
        }

        node.put("MsgBody",array);
        return node;
    }
}
