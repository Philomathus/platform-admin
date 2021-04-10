package com.qiqilm.server.admin.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.qiqilm.server.admin.mapper.ConfigDomainMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ConfigDomainCacheUtil {
	public static final String CONFIG_DOMAIN = "config:domain:";

	private static final Cache<String, String> DOMAIN_CACHE = CacheBuilder.newBuilder()
			//最大容量
			.maximumSize( 100 )
			//缓存过期时长
			.expireAfterWrite( 5, TimeUnit.SECONDS )
			// 设置并发级别为cpu核心数
			.concurrencyLevel( Runtime.getRuntime().availableProcessors() )
			.build();

	@Autowired
	private ConfigDomainMapper configDomainMapper;

	@Autowired
	private RedisUtil redisUtil;

	public boolean setValue( String code, String domain ) {
		return redisUtil.sAdd( CONFIG_DOMAIN + code, domain ) > 0;
	}

	public boolean deleteValue( String code, String domain ) {
		return redisUtil.sRemove( CONFIG_DOMAIN + code, domain ) > 0;
	}

	public void refreshKey( String code ) {
		List<String> domains = configDomainMapper.selectDomainsByCode( code );
		if ( CollectionUtils.isEmpty( domains ) ) {
			return;
		}
		redisUtil.unlink( CONFIG_DOMAIN + code );
		redisUtil.sAdd( CONFIG_DOMAIN + code, domains.toArray( new String[ 0 ] ) );
	}

	public String getValue( String code ) {
		String cacheInfo = DOMAIN_CACHE.getIfPresent( code );
		if ( StringUtils.isBlank( cacheInfo ) ) {
			if ( !redisUtil.exists( CONFIG_DOMAIN + code ) ) {
				this.refreshKey( code );
			}
			String domain = redisUtil.sRandom( CONFIG_DOMAIN + code );
			if ( domain == null ) {
				return null;
			}
			DOMAIN_CACHE.put( code, domain );
			return DOMAIN_CACHE.getIfPresent( code );
		}
		return cacheInfo;
	}


	public String dynamicValue( String value ) {
		String trim = value.trim();
		if ( trim.contains( "${" ) && trim.contains( "}" ) ) {
			String param  = trim.substring( trim.indexOf( "${" ) + 2, trim.indexOf( "}" ) );
			String domain = this.getValue( param );
			if ( StringUtils.isNotBlank( domain ) ) {
				return trim.replace( "${" + param + "}", domain );
			}
		}
		return trim;
	}
}
