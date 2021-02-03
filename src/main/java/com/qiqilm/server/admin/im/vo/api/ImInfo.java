package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.Objects;

@Data
public class ImInfo implements BaseFuc {
	private String Identifier;

	public static ImInfo of( LiveUser user ) {
		ImInfo imInfo = new ImInfo();
		imInfo.setIdentifier( user.getId().toString() );
		return imInfo;
	}

	public static ImInfo of( String id ) {
		ImInfo imInfo = new ImInfo();
		imInfo.setIdentifier( id );
		return imInfo;
	}


	@Override
	public String getApi() {
		return "/im_open_login_svc/account_import";
	}

	@Override
	public ObjectNode get() {
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode   node   = mapper.createObjectNode();
		if ( Objects.nonNull( Identifier ) )
			node.put( "Identifier", Identifier );
		return node;
	}
}
