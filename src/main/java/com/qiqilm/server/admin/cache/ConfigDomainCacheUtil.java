package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.mapper.ConfigDomainMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigDomainCacheUtil {
	public static final String CONFIG_DOMAIN = "config:domain:";

	@Autowired
	private ConfigDomainMapper configDomainMapper;

	@Autowired
	private RedisUtil redisUtil;

	public boolean setValue( String code,String domain ) {
		return redisUtil.sAdd( CONFIG_DOMAIN + code, domain ) > 0;
	}

	public boolean deleteValue( String code, String domain ) {
		return redisUtil.sRemove( CONFIG_DOMAIN + code, domain ) > 0;
	}

	public boolean refreshKey( String code ) {
		List<String> domains = configDomainMapper.selectDomainsByCode( code );
		redisUtil.unlink( CONFIG_DOMAIN + code );
		return redisUtil.sAdd( CONFIG_DOMAIN + code, domains.toArray( new String[ 0 ] ) ) > 0;
	}

	public String getValue( String code ) {
		return redisUtil.sRandom( CONFIG_DOMAIN + code );
	}
}
