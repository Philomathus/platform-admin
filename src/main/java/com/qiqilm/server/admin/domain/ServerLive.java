package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 直播流服务配置对象 server_live
 *
 * @author 77tv
 * @date 2021-01-27
 */
public class ServerLive extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 服务商
	 */
	private Integer provider;

	/**
	 * secretId
	 */
	private String secretId;

	/**
	 * Secretkey
	 */
	private String secretKey;

	/**
	 * 推流域名
	 */
	private String pushDomain;

	/**
	 * 拉流域名
	 */
	private String pullDomain;

	/**
	 * 推流防盗Key
	 */
	private String securityKey;

	/**
	 * licence下载地址
	 */
	private String licenceUrl;

	/**
	 * licence密钥
	 */
	private String licenceKey;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 拉流Key
	 */
	private String pullKey;

	/**
	 * 推流Key
	 */
	private String pushKey;

	/**
	 * 在线主播
	 */
	private Integer countNum;

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

	public String getSecretId() {
		return secretId;
	}

	public void setSecretId( String secretId ) {
		this.secretId = secretId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey( String secretKey ) {
		this.secretKey = secretKey;
	}

	public String getPushDomain() {
		return pushDomain;
	}

	public void setPushDomain( String pushDomain ) {
		this.pushDomain = pushDomain;
	}

	public String getPullDomain() {
		return pullDomain;
	}

	public void setPullDomain( String pullDomain ) {
		this.pullDomain = pullDomain;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey( String securityKey ) {
		this.securityKey = securityKey;
	}

	public String getLicenceUrl() {
		return licenceUrl;
	}

	public void setLicenceUrl( String licenceUrl ) {
		this.licenceUrl = licenceUrl;
	}

	public String getLicenceKey() {
		return licenceKey;
	}

	public void setLicenceKey( String licenceKey ) {
		this.licenceKey = licenceKey;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus( Integer status ) {
		this.status = status;
	}

	public String getPullKey() {
		return pullKey;
	}

	public void setPullKey( String pullKey ) {
		this.pullKey = pullKey;
	}

	public String getPushKey() {
		return pushKey;
	}

	public void setPushKey( String pushKey ) {
		this.pushKey = pushKey;
	}

	public Integer getCountNum() {
		return countNum;
	}

	public void setCountNum( Integer countNum ) {
		this.countNum = countNum;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "name", getName() )
				.append( "provider", getProvider() )
				.append( "secretId", getSecretId() )
				.append( "secretKey", getSecretKey() )
				.append( "pushDomain", getPushDomain() )
				.append( "pullDomain", getPullDomain() )
				.append( "securityKey", getSecurityKey() )
				.append( "licenceUrl", getLicenceUrl() )
				.append( "licenceKey", getLicenceKey() )
				.append( "status", getStatus() )
				.append( "pullKey", getPullKey() )
				.append( "pushKey", getPushKey() )
				.append( "countNum", getCountNum() )
				.toString();
	}
}
