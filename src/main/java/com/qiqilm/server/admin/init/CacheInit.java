package com.qiqilm.server.admin.init;

import com.qiqilm.server.admin.cache.MemberCacheManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CacheInit implements ApplicationListener<ContextRefreshedEvent> {
	@Autowired
	private MemberCacheManager cacheManager;

	@Override
	public void onApplicationEvent( ContextRefreshedEvent event ) {
		cacheManager.init();
	}
}
