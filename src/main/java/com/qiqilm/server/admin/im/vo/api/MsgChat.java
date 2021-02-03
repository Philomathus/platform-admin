package com.qiqilm.server.admin.im.vo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.im.BaseFuc;
import com.qiqilm.server.admin.utils.DateFormatUtils;

import java.util.Date;

/**
 * @Author kehai
 * @Date 2020/8/31 8:59
 * @Version 1.0
 */
public class MsgChat implements BaseFuc {

	private String ChatType;
	private Date   MsgTime;

	@Override
	public String getApi() {
		return "/open_msg_svc/get_history";
	}

	@Override
	public ObjectNode get() {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode   objectNode   = objectMapper.createObjectNode();
		objectNode.put( "ChatType", ChatType );
		objectNode.put( "MsgTime", DateFormatUtils.formate( MsgTime, "yyyyMMddHH" ) );
		return objectNode;
	}
}
