package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import com.qiqilm.server.admin.im.GroupType;
import lombok.Data;

@Data
public class CreateGroup implements BaseFuc {
	private String    Owner_Account;
	private GroupType Type;
	private String    Name;


	@Override
	public String getApi() {
		return "/group_open_http_svc/create_group";
	}

	@Override
	public ObjectNode get() {
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode   node   = mapper.createObjectNode();
		node.put( "Owner_Account", Owner_Account );
		node.put( "Type", Type.toString() );
		node.put( "Name", Name );
		return node;
	}
}
