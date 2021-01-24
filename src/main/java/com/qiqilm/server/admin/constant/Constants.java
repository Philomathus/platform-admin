package com.qiqilm.server.admin.constant;

import java.time.Duration;

/**
 * <p>Title: Constants</p>
 * <p>Description: 常量类</p>
 *
 * @author admin
 */
public abstract class Constants {

	/**
	 * 应答码：成功
	 */
	public static final int     URC_SUCCESS                = 0;
	/**
	 * 应答码：失败
	 */
	public static final int     URC_FAILURE                = 400;
	/**
	 * 直播
	 */
	public static final String  LIVE_PREX                  = "live:";
	public static final String  LIVE_PREX_CONFIG           = LIVE_PREX + "autoCache:LiveMConfig:map";
	//token  过期时间
	public static final Integer session_valid_time         = 3600 * 7 * 24;
	//sign  过期时间
	public static final Integer sign_valid_time            = 3600 * 10 * 24;
	public static final String  KAPTCHA_KEY                = "CX:kaptchaKey:";
	public static final Integer KAPTCHA_KEY_VALID_TIME     = 120;
	public static final String  TOKEN                      = "token";
	public static final String  CX_MENBER_CODE             = "cx_code";
	public static final Long    INIT_MEMBERCODE            = 10000L;
	public static final String  CX_GAME                    = "CX:game:";
	public static final String  CX_VIP                     = "CX:vip:";
	public static final String  CX_WEB_SET                 = "CX:webSet:";
	public static final String  REDIS_KEY_LOGIN_TOKEN_INFO = "CX:loginTokenInfo:";
	public static final String  REDIS_KEY_LOGIN_ADMIN_INFO = "CX:loginAdminInfo:";
	public static final String  PLATFORM_TOKEN             = "CX:platform:token:";
	public static final String  SESSION_CLICK_TIME         = "CX:platform:click:";
	public static final String  SESSION_CLICK_LOCK         = "CX:platform:lock:";
	public static final String  USER_TOKEN_KEY             = "CX:platform:user-token:";
	public static final String  TOKEN_USER_KEY             = "CX:platform:token-user:";
	public static final String  ADMIN_SIGN                 = LIVE_PREX + "admin-sign:";
	public static final String  TIME                       = "time";

	public static final Duration SESSION_VALID_TIME_DURATION = Duration.ofHours( 1 );

	public static final String DISCOUNT_BILL_LIMIT = "0.5";

	private Constants() {
		throw new RuntimeException( "Constants.class can't be instantiated" );
	}
}
