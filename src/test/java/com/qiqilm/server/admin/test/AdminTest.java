package com.qiqilm.server.admin.test;

import com.qiqilm.server.admin.PlatformAdminApplication;
import com.qiqilm.server.admin.task.MemberGiveCacheCleanTask;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@Log4j2
@SpringBootTest( classes = { PlatformAdminApplication.class } )// 指定启动类
@ActiveProfiles( "7700" )
public class AdminTest {
	@Autowired
	private MemberGiveCacheCleanTask memberGiveCacheCleanTask;
	@Autowired
	private StringRedisTemplate      stringRedisTemplate;

	@Test
	public void run() {
		// memberGiveCacheCleanTask.cleanGiveCache();
		Object o = stringRedisTemplate.opsForHash().get( MemberGiveCacheCleanTask.CACHE_GIVE_MONEY_MAP,
				"CX002_40105" );
		System.out.println(o == null ? "null" : o.toString());
	}
}
