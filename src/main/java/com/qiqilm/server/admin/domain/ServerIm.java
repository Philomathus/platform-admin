package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * IM即时通讯服务配置对象 server_im
 *
 * @author 77tv
 * @date 2021-01-27
 */
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

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId( String appId ) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey( String appKey ) {
		this.appKey = appKey;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify( String identify ) {
		this.identify = identify;
	}

	public String getFullGroup() {
		return fullGroup;
	}

	public void setFullGroup( String fullGroup ) {
		this.fullGroup = fullGroup;
	}

	public String getOnlineGroup() {
		return onlineGroup;
	}

	public void setOnlineGroup( String onlineGroup ) {
		this.onlineGroup = onlineGroup;
	}

	public Integer getProvider() {
		return provider;
	}

	public void setProvider( Integer provider ) {
		this.provider = provider;
	}

	public Integer getIsEffect() {
		return isEffect;
	}

	public void setIsEffect( Integer isEffect ) {
		this.isEffect = isEffect;
	}

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
}
