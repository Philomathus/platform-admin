package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.ServerIm;
import com.qiqilm.server.admin.mapper.ServerImMapper;
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
public class ServerImCacheUtil {
	public static final String SERVER_IM = Constants.LIVE_PREX + "serverIm:effect";

	@Autowired
	private ServerImMapper serverImMapper;
	@Autowired
	private RedisUtil      redisUtil;

	public void setServerIm( ServerIm serverIm ) {
		Map<String, String> serverImMap = new HashMap<>();
		for ( String code : serverIm.toCodes() ) {
			serverImMap.put( code, serverIm.getVal( code ) );
		}
		redisUtil.unlink( SERVER_IM );
		redisUtil.hMSet( SERVER_IM, serverImMap );
	}

	public String getValue( String code ) {
		this.exists();
		Object codeValue = redisUtil.hGet( SERVER_IM, code );
		return codeValue == null ? "" : codeValue.toString();
	}

	private void exists() {
		if ( !redisUtil.exists( SERVER_IM ) ) {
			ServerIm serverIm = serverImMapper.selectServerImByEffect().get( 0 );
			if ( serverIm != null ) {
				this.setServerIm( serverIm );
			}
		}
	}
}
