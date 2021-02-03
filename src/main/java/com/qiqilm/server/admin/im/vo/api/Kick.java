package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

@Data
public class Kick implements BaseFuc {

	private String Identifier;

	@Override
	public String getApi() {
		return "/im_open_login_svc/kick";
	}

	@Override
	public ObjectNode get() {
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode   node   = mapper.createObjectNode();
		node.put( "Identifier", Identifier );
		return node;
	}
}
