package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * oss文件存储服务配置对象 server_oss
 *
 * @author 77tv
 * @date 2021-01-27
 */
public class ServerOss extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * accessKey
	 */
	private String accessKey;

	/**
	 * accessSecret
	 */
	private String accessSecret;

	/**
	 * 访问域名
	 */
	private String endpoint;

	/**
	 * 文件存储
	 */
	private String bucket;

	/**
	 * 加速域名
	 */
	private String vhost;

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

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey( String accessKey ) {
		this.accessKey = accessKey;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret( String accessSecret ) {
		this.accessSecret = accessSecret;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint( String endpoint ) {
		this.endpoint = endpoint;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket( String bucket ) {
		this.bucket = bucket;
	}

	public String getVhost() {
		return vhost;
	}

	public void setVhost( String vhost ) {
		this.vhost = vhost;
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
				.append( "accessKey", getAccessKey() )
				.append( "accessSecret", getAccessSecret() )
				.append( "endpoint", getEndpoint() )
				.append( "bucket", getBucket() )
				.append( "vhost", getVhost() )
				.append( "isEffect", getIsEffect() )
				.append( "createBy", getCreateBy() )
				.append( "createTime", getCreateTime() )
				.append( "updateBy", getUpdateBy() )
				.append( "updateTime", getUpdateTime() )
				.toString();
	}
}
