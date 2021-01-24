package com.qiqilm.server.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author 77tv
 */
@Component
@ConfigurationProperties( prefix = "tv77" )
public class Tv77Config {
	/**
	 * 上传路径
	 */
	private static String  profile;
	/**
	 * 项目名称
	 */
	private        String  name;
	/**
	 * 版本
	 */
	private        String  version;
	/**
	 * 版权年份
	 */
	private        String  copyrightYear;

	public static String getProfile() {
		return profile;
	}

	public void setProfile( String profile ) {
		Tv77Config.profile = profile;
	}

	/**
	 * 获取头像上传路径
	 */
	public static String getAvatarPath() {
		return getProfile() + "/avatar";
	}

	/**
	 * 获取下载路径
	 */
	public static String getDownloadPath() {
		return getProfile() + "/download/";
	}

	/**
	 * 获取上传路径
	 */
	public static String getUploadPath() {
		return getProfile() + "/upload";
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion( String version ) {
		this.version = version;
	}

	public String getCopyrightYear() {
		return copyrightYear;
	}

	public void setCopyrightYear( String copyrightYear ) {
		this.copyrightYear = copyrightYear;
	}
}
