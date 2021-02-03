package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.Set;

@Data
public class GroupInfo implements BaseFuc {
	private Set<String> groupIds;

	@Override
	public String getApi() {
		return "/group_open_http_svc/get_group_info";
	}

	@Override
	public ObjectNode get() {
		final ObjectMapper mapper       = new ObjectMapper();
		final ObjectNode   node         = mapper.createObjectNode();
		ArrayNode          groupIdArray = mapper.createArrayNode();
		for ( String groupId : groupIds ) {
			groupIdArray.add( groupId );
		}
		node.set( "GroupIdList", groupIdArray );
		final ObjectNode responseFilter      = mapper.createObjectNode();
		ArrayNode        groupBaseInfoFilter = mapper.createArrayNode();

		groupBaseInfoFilter.add( "GroupId" );
		groupBaseInfoFilter.add( "Type" );
		groupBaseInfoFilter.add( "Name" );
		groupBaseInfoFilter.add( "Owner_Account" );
		groupBaseInfoFilter.add( "CreateTime" );
		groupBaseInfoFilter.add( "LastInfoTime" );
		groupBaseInfoFilter.add( "MemberNum" );
		groupBaseInfoFilter.add( "MaxMemberNum" );
		groupBaseInfoFilter.add( "ApplyJoinOption" );

		responseFilter.set( "GroupBaseInfoFilter", groupBaseInfoFilter );
		node.set( "ResponseFilter", responseFilter );
		return node;
	}
}
