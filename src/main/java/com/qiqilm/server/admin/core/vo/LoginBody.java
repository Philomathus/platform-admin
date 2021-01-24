package com.qiqilm.server.admin.core.vo;

import lombok.Data;

/**
 * 用户登录对象
 *
 * @author 77tv
 */
@Data
public class LoginBody {
	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 用户密码
	 */
	private String password;

	/**
	 * 验证码
	 */
	private Integer googleAuthCode;

	/**
	 * 唯一标识
	 */
	private String uuid = "";
}
