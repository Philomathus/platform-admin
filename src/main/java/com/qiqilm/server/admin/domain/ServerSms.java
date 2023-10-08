package com.qiqilm.server.admin.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.Nullable;

import java.util.Date;

/**
 * SMS短信服务配置对象 server_sms
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
public class ServerSms {
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

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 分类密钥
	 */
	private String endpoint;

	/**
	 * 分类的秘密
	 */
	private String classificationKey;

	/**
	 * 服务器比例
	 * sql: ratio DOUBLE default 0 null;
	 */
	@Nullable
	private Double ratio;

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
				.append( "endpoint", getEndpoint())
				.append( "classificationKey", getClassificationKey())
				.append( "ratio", getRatio())
				.toString();
	}
}
