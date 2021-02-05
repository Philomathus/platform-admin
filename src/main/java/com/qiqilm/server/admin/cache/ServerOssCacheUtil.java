package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.ServerOss;
import com.qiqilm.server.admin.mapper.ServerOssMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
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
public class ServerOssCacheUtil {
	public static final String SERVER_OSS = Constants.CONFIG_PREX + "serverOss:effect";

	@Autowired
	private RedisUtil       redisUtil;
	@Autowired
	private ServerOssMapper serverOssMapper;

	public void setServerOss( ServerOss serverOss ) {
		Map<String, String> serverOssMap = new HashMap<>();
		for ( String code : serverOss.toCodes() ) {
			serverOssMap.put( code, serverOss.getVal( code ) );
		}
		redisUtil.unlink( SERVER_OSS );
		redisUtil.hMSet( SERVER_OSS, serverOssMap );
	}

	public String getValue( String code ) {
		this.exists();
		Object codeValue = redisUtil.hGet( SERVER_OSS, code );
		return codeValue == null ? "" : codeValue.toString();
	}

	public ServerOss getAllValue() {
		this.exists();
		Map<Object, Object> resultMap = redisUtil.hGetAll( SERVER_OSS );
		if ( resultMap.isEmpty() ) {
			return null;
		}
		return JsonUtil.map2Object( resultMap, ServerOss.class );
	}

	private void exists() {
		if ( !redisUtil.exists( SERVER_OSS ) ) {
			ServerOss serverOss = serverOssMapper.selectServerOssByEffect();
			if ( serverOss != null ) {
				this.setServerOss( serverOss );
			}
		}
	}

	public void clear(){
		redisUtil.unlink( SERVER_OSS );
	}
}
