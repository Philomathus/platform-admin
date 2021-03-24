package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.enums.PlatformUserKey;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author qicheng
 */
@Component
public class MemberForbidUtil {
	public static final String CACHE_USER_FORBID         = Constants.LIVE_PREX + "user:forbid:";
	public static final String CACHE_USER_SPEAK_INTERVAL = Constants.LIVE_PREX + "user:speakInterval:";

	@Autowired
	private RedisUtil redisUtil;

	public void setUserForbid( String pUserId, Integer videoId, Duration forbidTime ) {
		redisUtil.strSet( CACHE_USER_FORBID + pUserId, videoId.toString(), forbidTime );
	}

	public long getUserForbidExpire( String pUserId ) {
		long expire = redisUtil.getExpire( CACHE_USER_FORBID + pUserId );
		return expire > 0 ? expire : 0;
	}

	public void setUserSpeakInterval( String pUserId, Integer videoId, Duration forbidTime ) {
		redisUtil.strSet( CACHE_USER_SPEAK_INTERVAL + pUserId, videoId.toString(), forbidTime );
	}

	public long getUserSpeakIntervalExpire( String pUserId ) {
		long expire = redisUtil.getExpire( CACHE_USER_SPEAK_INTERVAL + pUserId );
		return expire > 0 ? expire : 0;
	}

	public boolean setPlatformUserSpeak( String pUserId, boolean speak ) {
		String token = redisUtil.strGet( Constants.USER_TOKEN_KEY + pUserId );
		if ( Strings.isBlank( token ) ) {
			return false;
		}
		redisUtil.hSet( Constants.TOKEN_USER_KEY + token, PlatformUserKey.SPEAK.getKey(), speak + "" );
		return true;
	}
	public int setPlatformUserStatus( String pUserId, int status ) {
		String token = redisUtil.strGet( Constants.USER_TOKEN_KEY + pUserId );
		if ( Strings.isBlank( token ) ) {
			return status;
		}
		redisUtil.hSet( Constants.TOKEN_USER_KEY + token, PlatformUserKey.STATUS.getKey(), status + "" );
		return status;
	}
}
