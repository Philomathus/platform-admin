package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * oss文件存储服务配置对象 server_oss
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
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

	public String[] toCodes() {
		return new String[]{ "id", "name", "accessKey", "accessSecret", "endpoint", "bucket" };
	}

	public String getVal( String code ) {
		switch ( code ) {
		case "id":
			return id + "";
		case "name":
			return name;
		case "accessKey":
			return accessKey;
		case "accessSecret":
			return accessSecret;
		case "endpoint":
			return endpoint;
		case "bucket":
			return bucket;
		default:
			return null;
		}
	}
}
