package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.ServerSms;
import com.qiqilm.server.admin.mapper.ServerSmsMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class ServerSmsCacheUtil {
	public static final String SERVER_SMS = Constants.CONFIG_PREX + "serverSms:effect";

	@Autowired
	private RedisUtil       redisUtil;
	@Autowired
	private ServerSmsMapper serverSmsMapper;

	public void setServerSms( ServerSms serverSms ) {
		Map<String, String> serverSmsMap = new HashMap<>();
		for ( String code : serverSms.toCodes() ) {
			serverSmsMap.put( code, serverSms.getVal( code ) );
		}
		redisUtil.unlink( SERVER_SMS );
		redisUtil.hMSet( SERVER_SMS, serverSmsMap );
	}

	public String getValue( String code ) {
		this.exists();
		Object codeValue = redisUtil.hGet( SERVER_SMS, code );
		return codeValue == null ? "" : codeValue.toString();
	}

	public ServerSms getAllValue() {
		this.exists();
		Map<Object, Object> resultMap = redisUtil.hGetAll( SERVER_SMS );
		if ( resultMap.isEmpty() ) {
			return null;
		}
		return JsonUtil.map2Object( resultMap, ServerSms.class );
	}

	private void exists() {
		if ( !redisUtil.exists( SERVER_SMS ) ) {
			List<ServerSms> serverSmsList = serverSmsMapper.selectServerSmsByEffect();
			if ( serverSmsList.isEmpty() ) {
				return;
			}
			ServerSms serverSms = serverSmsList.get( 0 );
			if ( serverSms != null ) {
				this.setServerSms( serverSms );
			}
		}
	}
}
