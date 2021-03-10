package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.ServerSms;
import com.qiqilm.server.admin.mapper.ServerSmsMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
		redisUtil.unlink( SERVER_SMS );
		redisUtil.strSet( SERVER_SMS, JsonUtil.object2Json( serverSms ) );
	}

	public ServerSms getEffect() {
		this.exists();
		String value = redisUtil.strGet( SERVER_SMS );
		return value == null ? null : JsonUtil.json2Object( value, ServerSms.class );
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

	public void clear(){
		redisUtil.unlink( SERVER_SMS );
	}
}
