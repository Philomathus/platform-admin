package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * SMS短信服务配置对象 server_sms
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
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

	public String[] toCodes() {
		return new String[]{ "id", "name", "provider", "appKey", "appAccess", "region", "signature", "template", "smsSdkAppid" };
	}

	public String getVal( String code ) {
		switch ( code ) {
		case "id":
			return id + "";
		case "name":
			return name;
		case "provider":
			return provider + "";
		case "appKey":
			return appKey;
		case "appAccess":
			return appAccess;
		case "region":
			return region;
		case "signature":
			return signature;
		case "template":
			return template;
		case "smsSdkAppid":
			return smsSdkAppid;
		default:
			return null;
		}
	}
}
