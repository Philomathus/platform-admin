package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.ServerLive;
import com.qiqilm.server.admin.mapper.ServerLiveMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class ServerLiveCacheUtil {
	private static final String SERVER_LIVE = Constants.CONFIG_PREX + "serverLive:";

	@Autowired
	private RedisUtil        redisUtil;
	@Autowired
	private ServerLiveMapper serverLiveMapper;

	public int getVideoType( int lineNumber ) {
		this.exists( lineNumber );
		Object videoType = redisUtil.hGet( SERVER_LIVE + lineNumber, "video_type" );
		return videoType == null ? 1 : Integer.parseInt( videoType.toString() );
	}

	public String getValue( int lineNumber, String code ) {
		this.exists( lineNumber );
		Object codeValue = redisUtil.hGet( SERVER_LIVE + lineNumber, code );
		return codeValue == null ? "" : codeValue.toString();
	}

	private void exists( long lineNumber ) {
		if ( !redisUtil.exists( SERVER_LIVE + lineNumber ) ) {
			ServerLive serverLive = serverLiveMapper.selectServerLiveById( lineNumber );
			if ( serverLive != null ) {
				this.setServerLive( serverLive );
			}
		}
	}

	public void setServerLive( ServerLive serverLive ) {
		Map<Object, Object> serverLiveMap = new HashMap<>();
		for ( String code : serverLive.toCodes() ) {
			serverLiveMap.put( code, serverLive.getVal( code ) );
		}
		redisUtil.unlink( SERVER_LIVE + serverLive.getId() );
		redisUtil.hMSet( SERVER_LIVE + serverLive.getId(), serverLiveMap );
	}

	public void clear( long id ) {
		redisUtil.unlink( SERVER_LIVE + id );
	}
}
