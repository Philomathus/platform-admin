package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupInfoItem {
	@JsonProperty( "GroupId" )
	private String groupId;
	@JsonProperty( "Type" )
	private String type;
	@JsonProperty( "Name" )
	private String name;

	@JsonProperty( "Owner_Account" )
	private String  ownerAccount;
	@JsonProperty( "CreateTime" )
	private Integer createTime;
	@JsonProperty( "LastInfoTime" )
	private Integer lastInfoTime;
	@JsonProperty( "MemberNum" )
	private Integer memberNum;
	@JsonProperty( "MaxMemberNum" )
	private Integer maxMemberNum;
	/**
	 * <h3>申请加群选项包括如下几种：</h3>
	 * <ul>
	 *     <li>DisableApply 表示禁止任何人申请加入</li>
	 *     <li>NeedPermission 表示需要群主或管理员审批</li>
	 *     <li>FreeAccess 表示允许无需审批自由加入群组</li>
	 * </ul>
	 */
	@JsonProperty( "ApplyJoinOption" )
	private String  applyJoinOption;
}
