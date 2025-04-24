package com.qiqilm.server.admin.constant;

/**
 * 通用常量信息
 *
 * @author 77tv
 */
public class AdminConstants {
	/**
	 * UTF-8 字符集
	 */
	public static final String UTF8 = "UTF-8";

	/**
	 * GBK 字符集
	 */
	public static final String GBK = "GBK";

	/**
	 * http请求
	 */
	public static final String HTTP = "http://";

	/**
	 * https请求
	 */
	public static final String HTTPS = "https://";

	/**
	 * 通用成功标识
	 */
	public static final String SUCCESS = "0";

	/**
	 * 通用失败标识
	 */
	public static final String FAIL = "1";

	/**
	 * 登录成功
	 */
	public static final String LOGIN_SUCCESS = "Success";

	/**
	 * 注销
	 */
	public static final String LOGOUT = "Logout";

	/**
	 * 登录失败
	 */
	public static final String LOGIN_FAIL = "Error";

	/**
	 * 登录用户 redis token key
	 */
	public static final String LOGIN_TOKEN_KEY = "platform:loginTokens:";
	/**
	 * 登录用户 redis user key
	 */
	public static final String LOGIN_USER_TOEN_KEY = "platform:loginUsers:";

	/**
	 * 防重提交 redis key
	 */
	public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

	/**
	 * 令牌
	 */
	public static final String TOKEN = "token";

	/**
	 * 令牌前缀
	 */
	public static final String TOKEN_PREFIX = "Bearer ";

	/**
	 * 令牌前缀
	 */
	public static final String LOGIN_USER_KEY = "login_user_key";

	/**
	 * 用户ID
	 */
	public static final String JWT_USERID = "userid";

	/**
	 * 用户名称
	 */
	public static final String JWT_USERNAME = "sub";

	/**
	 * 用户头像
	 */
	public static final String JWT_AVATAR = "avatar";

	/**
	 * 创建时间
	 */
	public static final String JWT_CREATED = "created";

	/**
	 * 用户权限
	 */
	public static final String JWT_AUTHORITIES = "authorities";

	/**
	 * 参数管理 cache key
	 */
	public static final String SYS_CONFIG_KEY = "sys_config:";

	/**
	 * 字典管理 cache key
	 */
	public static final String SYS_DICT_KEY = "sys_dict:";

	/**
	 * 资源映射路径 前缀
	 */
	public static final String RESOURCE_PREFIX = "/profile";

	/**
	 * 频繁访问,限流
	 */
	public static final String ACCESS_TIMES = "access_limit:";

	public static final String RESET_PASSWORD_CODE = "reset_password_code:";

	public static final int RESET_PASSWORD_CODE_LENGTH = 6;
}
