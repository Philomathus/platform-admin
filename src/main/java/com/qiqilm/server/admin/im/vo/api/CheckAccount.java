package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class CheckAccount implements BaseFuc {
	private List<String> CheckItem;

	@Override
	public String getApi() {
		return "/im_open_login_svc/account_check";
	}

	@Override
	public ObjectNode get() {
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode   node   = mapper.createObjectNode();
		final ArrayNode    array  = mapper.createArrayNode();

		if ( Objects.nonNull( CheckItem ) && !CheckItem.isEmpty() ) {
			for ( String item : CheckItem ) {
				final ObjectNode userId = mapper.createObjectNode();
				userId.put( "UserID", item );
				array.add( userId );
			}
		}

		node.put( "CheckItem", array );
		return node;
	}
}
