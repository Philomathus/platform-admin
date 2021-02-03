package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class MulAccountImport implements BaseFuc {

	private List<String> Accounts;

	@Override
	public String getApi() {
		return "/im_open_login_svc/multiaccount_import";
	}

	@Override
	public ObjectNode get() {
		final ObjectMapper mapper    = new ObjectMapper();
		final ObjectNode   node      = mapper.createObjectNode();
		final ArrayNode    arrayNode = mapper.createArrayNode();
		if ( Objects.nonNull( Accounts ) && !Accounts.isEmpty() ) {
			for ( String account : Accounts ) {
				arrayNode.add( account );
			}
		}
		node.put( "Accounts", arrayNode );
		return node;
	}
}
