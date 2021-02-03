package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

/**
 * 腾讯IM群禁言
 *
 * @author qicheng
 */
@Data
public class ForbidSendMsg implements BaseFuc {

	private String   groupId;    //拉取消息的群 ID
	private String[] accounts; // 最多支持500个
	private Integer  shutUpTime; // 禁言时间，单位为秒

	@Override
	public String getApi() {
		return "/group_open_http_svc/forbid_send_msg";
	}

	@Override
	public ObjectNode get() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode   node   = mapper.createObjectNode();
		node.put( "GroupId", groupId );
		ArrayNode array = mapper.createArrayNode();
		for ( String s : accounts ) {
			array.add( s );
		}
		node.set( "Members_Account", array );
		node.put( "ShutUpTime", shutUpTime );
		return node;
	}
}
