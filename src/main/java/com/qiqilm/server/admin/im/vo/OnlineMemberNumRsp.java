package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OnlineMemberNumRsp extends ImRsp {
	@JsonProperty( "OnlineMemberNum" )
	private Integer onlineMemberNum;

	@JsonProperty( "GroupId" )
	private String groupId;
}
