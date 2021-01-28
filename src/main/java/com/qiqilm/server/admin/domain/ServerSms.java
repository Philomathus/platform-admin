package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * SMS短信服务配置对象 server_sms
 *
 * @author 77tv
 * @date 2021-01-27
 */
public class ServerSms extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * SMS名称
	 */
	private String name;

	/**
	 * 服务商
	 */
	private Integer provider;

	/**
	 * appKey
	 */
	private String appKey;

	/**
	 * appAccess
	 */
	private String appAccess;

	/**
	 * 地区
	 */
	private String region;

	/**
	 * 签名
	 */
	private String signature;

	/**
	 * 模板
	 */
	private String template;

	/**
	 * smsSdkAppid
	 */
	private String smsSdkAppid;

	/**
	 * 管理员账号
	 */
	private String identify;

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

	public Integer getProvider() {
		return provider;
	}

	public void setProvider( Integer provider ) {
		this.provider = provider;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey( String appKey ) {
		this.appKey = appKey;
	}

	public String getAppAccess() {
		return appAccess;
	}

	public void setAppAccess( String appAccess ) {
		this.appAccess = appAccess;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion( String region ) {
		this.region = region;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature( String signature ) {
		this.signature = signature;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate( String template ) {
		this.template = template;
	}

	public String getSmsSdkAppid() {
		return smsSdkAppid;
	}

	public void setSmsSdkAppid( String smsSdkAppid ) {
		this.smsSdkAppid = smsSdkAppid;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify( String identify ) {
		this.identify = identify;
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
				.append( "provider", getProvider() )
				.append( "appKey", getAppKey() )
				.append( "appAccess", getAppAccess() )
				.append( "region", getRegion() )
				.append( "signature", getSignature() )
				.append( "template", getTemplate() )
				.append( "smsSdkAppid", getSmsSdkAppid() )
				.append( "identify", getIdentify() )
				.append( "isEffect", getIsEffect() )
				.toString();
	}
}
