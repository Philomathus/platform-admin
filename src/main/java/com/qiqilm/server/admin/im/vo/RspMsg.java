package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author kehai
 * @Date 2020/7/9 15:18
 * @Version 1.0
 */
@Data
public class RspMsg {
	@JsonProperty( "From_Account" )
	private String           fromAccount;
	@JsonProperty( "IsPlaceMsg" )
	private Integer          isPlaceMsg;
	@JsonProperty( "MsgBody" )
	private List<RspMsgBody> msgBody;
	@JsonProperty( "MsgPriority" )
	private String           msgPriority;
	@JsonProperty( "MsgRandom" )
	private Long             msgRandom;
	@JsonProperty( "MsgSeq" )
	private Long             msgSeq;
	@JsonProperty( "MsgTimeStamp" )
	private Long             msgTimeStamp;

}
