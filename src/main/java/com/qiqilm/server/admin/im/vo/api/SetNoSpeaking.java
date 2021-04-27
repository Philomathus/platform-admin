package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

@Data
public class SetNoSpeaking implements BaseFuc {

	private String Identifier;
	private long timeSec;

	@Override
	public String getApi() {
		return "/openconfigsvr/setnospeaking";
	}

	@Override
	public ObjectNode get() {
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode   node   = mapper.createObjectNode();
		node.put( "Set_Account", Identifier );
		node.put( "GroupmsgNospeakingTime", timeSec );
		return node;
	}
}
