package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.MessageType;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class SendGroupMsg implements BaseFuc {

	private String            groupId;
	private String            fromAccount;
	private List<MessageType> msgBody;

	@Override
	public String getApi() {
		return "/group_open_http_svc/send_group_msg";
	}

	@Override
	public ObjectNode get() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode   node   = mapper.createObjectNode();
		node.put( "MsgPriority", "High" );
		node.put( "GroupId", groupId );
		if ( Objects.nonNull( fromAccount ) )
			node.put( "From_Account", fromAccount );
		node.put( "Random", ( int ) ( Math.random() * 65535 ) );
		ArrayNode nodes = mapper.createArrayNode();
		for ( MessageType e : msgBody ) {
			nodes.add( e.getNode() );
		}
		ArrayNode msgCallback = mapper.createArrayNode();
		msgCallback.add( "ForbidBeforeSendMsgCallback" );
		node.put( "ForbidCallbackControl", msgCallback );
       /* "":[
        "",
                "ForbidAfterSendMsgCallback"],
        */
		node.put( "MsgBody", nodes );
		return node;
	}
}
