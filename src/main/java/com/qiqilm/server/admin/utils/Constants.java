package com.qiqilm.server.admin.utils;

/**
 * <p>Title: Constants</p>
 * <p>Description: 常量类</p>
 *
 * @author admin
 */
public abstract class Constants {

	private Constants() {
		throw new RuntimeException( "Constants.class can't be instantiated" );
	}

	/**
	 * 应答码：成功
	 */
	public static final int URC_SUCCESS = 0;

	public static final int insufficient_authority = 4;

	public static final Integer session_valid_time = 3600 * 24 * 6;

	public static final Integer no_login = 402;

	public static final String redisPRex = "live:";

	public static final String TOKEN      = "token";
	// 主播token取userId
	public static final String TOKEN_HOST = redisPRex + "token-host:";
	// 主播userId取token
	public static final String HOST_TOKEN = redisPRex + "host-token:";

	// 平台环境变量
	public static final String CX_WEB_SET                 = "CX:webSet:";
	// 平台后台管理员token
	public static final String REDIS_KEY_LOGIN_TOKEN_INFO = "CX:loginTokenInfo:";
	public static final String REDIS_KEY_LOGIN_ADMIN_INFO = "CX:loginAdminInfo:";
	// 平台用户token
	public static final String TOKEN_USER_KEY             = "CX:platform:token-user:";
	public static final String USER_TOKEN_KEY             = "CX:platform:user-token:";

	public static final String REDIS_KEY_DETECT_PLAY = redisPRex + "liveVideo:detectPlay";
	// 主播token头
	public static final String HOST_TOKEN_NAME       = "host-";

	// 主播签名
	public static final String HOST_SIGN  = redisPRex + "host-sign:";
	public static final String USER_SIGN  = redisPRex + "user-sign:";
	public static final String ADMIN_SIGN = redisPRex + "admin-sign:";

	// 直播间机器人投注信息
	public static final String HOST_BET = redisPRex + "host-robot-bet:";
	// 坐骑配置
	public static final String LIVE_MOUNT = redisPRex + "mount";
	// 进入日志临时缓存
	public static final String LIVEENTERLOG = redisPRex + "live-enter-log";

}
