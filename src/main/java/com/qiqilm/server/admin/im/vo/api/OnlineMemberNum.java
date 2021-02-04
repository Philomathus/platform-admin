package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

@Data
public class OnlineMemberNum implements BaseFuc {
	private String groupId;

	@Override
	public String getApi() {
		return "/group_open_http_svc/get_online_member_num";
	}

	@Override
	public ObjectNode get() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode   node   = mapper.createObjectNode();
		node.put( "GroupId", groupId );
		return node;
	}
}
