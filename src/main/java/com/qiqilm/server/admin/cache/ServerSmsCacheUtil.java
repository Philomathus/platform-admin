package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.ServerSms;
import com.qiqilm.server.admin.mapper.ServerSmsMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.StringUtils;
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
	private static final String SERVER_SMS_LIST = Constants.CONFIG_PREX + "serverSms:list";
	private static final String SERVER_SMS_HASH = Constants.CONFIG_PREX + "serverSms:hash";

	@Autowired
	private RedisUtil       redisUtil;
	@Autowired
	private ServerSmsMapper serverSmsMapper;

    public long countCache(){
        return redisUtil.lSize( SERVER_SMS_LIST );
    }
	public void setServerSmsCache( ServerSms serverSms ) {
		String smsId = serverSms.getId().toString();
		Long   size  = redisUtil.lSize( SERVER_SMS_LIST );
		if ( size > 0 ) {
			List<String> keys = redisUtil.lRange( SERVER_SMS_LIST, 0, size - 1 );
			if ( keys.contains( smsId ) ) {
				redisUtil.hSet( SERVER_SMS_HASH, smsId, JsonUtil.object2Json( serverSms ) );
				return;
			}
		}
		redisUtil.lRightPush( SERVER_SMS_LIST, smsId );
		redisUtil.hSet( SERVER_SMS_HASH, smsId, JsonUtil.object2Json( serverSms ) );
	}

	public ServerSms getServerSmsCache( long index ) {
		this.existsCache();
		String smsId = redisUtil.lIndex( SERVER_SMS_LIST, index );
		if ( StringUtils.isBlank( smsId ) ) {
			if ( index == 0 ) {
				return null;
			} else {
				return this.getServerSmsCache( 0 );
			}
		}
		Object value = redisUtil.hGet( SERVER_SMS_HASH, smsId );
		return value == null ? null : JsonUtil.json2Object( value.toString(), ServerSms.class );
	}

	private void existsCache() {
		if ( redisUtil.lSize( SERVER_SMS_LIST ) == 0 ) {
			List<ServerSms> serverSmsList = serverSmsMapper.selectServerSmsByEffect();
			if ( serverSmsList.isEmpty() ) {
				return;
			}
			for ( ServerSms serverSms : serverSmsList ) {
				redisUtil.lRightPush( SERVER_SMS_LIST, serverSms.getId().toString() );
				redisUtil.hSet( SERVER_SMS_HASH, serverSms.getId().toString(), JsonUtil.object2Json( serverSms ) );
			}
		}
	}

	public void clearCache( long smsId ) {
		redisUtil.lDelete( SERVER_SMS_LIST, 0, smsId + "" );
		redisUtil.hDelete( SERVER_SMS_HASH, smsId + "" );
	}
}
