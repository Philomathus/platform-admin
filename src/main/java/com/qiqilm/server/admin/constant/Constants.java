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
	public static final String  CONFIG_PREX                = "config:";
	public static final String  LIVE_PREX_CONFIG           = "autoCache:LiveMConfig:map";
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
	public static final String  REDIS_KEY_LOGIN_TOKEN_INFO = "CX:loginTokenInfo:";
	public static final String  REDIS_KEY_LOGIN_ADMIN_INFO = "CX:loginAdminInfo:";
	public static final String  PLATFORM_TOKEN             = "CX:platform:token:";
	public static final String  SESSION_CLICK_TIME         = "CX:platform:click:";
	public static final String  SESSION_CLICK_LOCK         = "CX:platform:lock:";
	public static final String  ADMIN_LOCK                 = "CX:admin:lock:";
	public static final String  USER_TOKEN_KEY             = "CX:platform:user-token:";
	public static final String  TOKEN_USER_KEY             = "CX:platform:token-user:";
	public static final String  CX_HOME_NOTICE             = "CX:home-notices";
	public static final String  CX_HOME_BANNER             = "CX:home-banners";

	// 主播token取userId
	public static final String  TOKEN_HOST                 = LIVE_PREX + "token-host:";
	// 主播userId取token
	public static final String  HOST_TOKEN                 = LIVE_PREX + "host-token:";

	// 主播签名
	public static final String  HOST_SIGN                  = LIVE_PREX + "host-sign:";
	public static final String  USER_SIGN                  = LIVE_PREX + "user-sign:";
	public static final String  ADMIN_SIGN                 = LIVE_PREX + "admin-sign:";

	public static final String REDIS_KEY_DETECT_PLAY = LIVE_PREX + "liveVideo:detectPlay";

	public static final String   TIME                        = "time";
	public static final Duration SESSION_VALID_TIME_DURATION = Duration.ofHours( 1 );
	public static final String   DISCOUNT_BILL_LIMIT         = "0.5";

	// 直播间机器人投注信息
	public static final String HOST_BET     = LIVE_PREX + "host-robot-bet:";
	// 坐骑配置
	public static final String LIVE_MOUNT   = LIVE_PREX + "mount";
	// 进入日志临时缓存
	public static final String LIVEENTERLOG = LIVE_PREX + "live-enter-log";

	public static final String  LIVE_HOST_LOCK                 = LIVE_PREX + "lock:";

	public static final String PAY_AGENT_TOKEN_USER = "Pay:agentAccount:token-user:";
	public static final String PAY_AGENT_USER_TOKEN = "Pay:agentAccount:user-token:";

	private Constants() {
		throw new RuntimeException( "Constants.class can't be instantiated" );
	}
}
