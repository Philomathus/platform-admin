package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GroupInfoRsp extends ImRsp {
	@JsonProperty("GroupInfo")
	List<GroupInfoItem> groupInfo;
}
