package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class LiveCacheUtil {
	@Autowired
	private RedisUtil redisUtil;

	public void setRedis( String key, String identify, Object value ) {
		String valStr;
		if ( value instanceof String ) {
			valStr = ( String ) value;
		} else {
			valStr = JsonUtil.object2Json( value );
		}
		redisUtil.strSet( Constants.LIVE_PREX + key + ":" + identify, valStr );
	}

	public void setRedis( String key, String identify, Object value, int seconds ) {
		String valStr;
		if ( value instanceof String ) {
			valStr = ( String ) value;
		} else {
			valStr = JsonUtil.object2Json( value );
		}
		redisUtil.strSet( Constants.LIVE_PREX + key + ":" + identify, valStr, Duration.ofSeconds( seconds ) );
	}

	public String getRedis( String key, String identify ) {
		return redisUtil.strGet( Constants.LIVE_PREX + key + ":" + identify );
	}

	public void removeRedis( String key, String identify ) {
		redisUtil.unlink( Constants.LIVE_PREX + key + ":" + identify );
	}

	public List<Object> getConf( List<Object> codes ) {
		String  keys   = "autoCache:LiveMConfig:map";
		Boolean exists = redisUtil.exists( keys );
		if ( exists == null || !exists ) {
			this.refreshConfCache();
		}
		return redisUtil.hMGet( keys, codes );
	}

	public String getConf( String code, String defaultValue ) {
		String  keys   = "autoCache:LiveMConfig:map";
		Boolean exists = redisUtil.exists( keys );
		if ( exists == null || !exists ) {
			this.refreshConfCache();
		}
		Object value = redisUtil.hGet( keys, code );
		return value == null ? defaultValue : value.toString().trim();
	}

	public void refreshConfCache() {
		String              keys = "autoCache:LiveMConfig:map";
		Map<Object, Object> map  = new HashMap<>();
		redisUtil.hMSet( keys, map );
		redisUtil.expire( keys, Duration.ofDays( 1 ) );
	}

	public String getConf( String code ) {
		return getConf( code, "" );
	}

	public BigDecimal getConfBd( String code ) {
		try {
			return new BigDecimal( getConf( code, "0" ) );
		} catch ( NumberFormatException e ) {
			return BigDecimal.ZERO;
		}
	}

	public int getConfInt( String code ) {
		try {
			return Integer.parseInt( getConf( code, "0" ) );
		} catch ( NumberFormatException e ) {
			return 0;
		}
	}

	public boolean getConfBool( String code ) {
		return getConfInt( code ) > 0;
	}

}
