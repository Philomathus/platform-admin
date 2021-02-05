package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * IM即时通讯服务配置对象 server_im
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
public class ServerIm extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * appId
	 */
	private String appId;

	/**
	 * appKey
	 */
	private String appKey;

	/**
	 * 管理员账号
	 */
	private String identify;

	/**
	 * 全员组
	 */
	private String fullGroup;

	/**
	 * 在线组
	 */
	private String onlineGroup;

	/**
	 * 服务商
	 */
	private Integer provider;

	/**
	 * 状态
	 */
	private Integer isEffect;

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "name", getName() )
				.append( "appId", getAppId() )
				.append( "appKey", getAppKey() )
				.append( "identify", getIdentify() )
				.append( "fullGroup", getFullGroup() )
				.append( "onlineGroup", getOnlineGroup() )
				.append( "provider", getProvider() )
				.append( "isEffect", getIsEffect() )
				.toString();
	}

	public String[] toCodes() {
		return new String[]{ "tim_sdkappid", "tim_sdk_key", "full_group_id", "on_line_group_id", "tim_identifier" };
	}

	public String getVal(String code) {
		switch (code){
		case "tim_sdkappid":
			return appId;
		case "tim_sdk_key":
			return appKey;
		case "tim_identifier":
			return identify;
		case "full_group_id":
			return fullGroup;
		case "on_line_group_id":
			return onlineGroup;
		default:
			return null;
		}
	}
}
