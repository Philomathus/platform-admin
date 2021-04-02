package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class MemberGiveCacheCleanTask {
	public static final String CACHE_GIVE_MONEY_MAP = Constants.LIVE_PREX + "user:giveMoney";

	@Autowired
	private RedisUtil           redisUtil;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private MemberInfoMapper    memberInfoMapper;

	// 每天凌晨执行清理任务
	//@Scheduled( cron = "0 0 * * * ?" )
	public void cleanGiveCache() {
		List<String> list = stringRedisTemplate.execute( ( RedisCallback<List<String>> ) connection -> {
			List<String> binaryKeys = new ArrayList<>();
			Cursor<Map.Entry<byte[], byte[]>> cursor = connection.hScan( CACHE_GIVE_MONEY_MAP.getBytes(),
					new ScanOptions.ScanOptionsBuilder().count( 100 ).build() );
			while ( cursor.hasNext() ) {
				Map.Entry<byte[], byte[]> entry = cursor.next();
				double                    value = Double.parseDouble( new String( entry.getValue() ) );
				if ( value >= 100 ) {
					binaryKeys.add( new String( entry.getKey() ) );
				}
			}
			return binaryKeys;
		} );

		int totalcount = list.size();
		int pagesize   = 20;
		int pagecount  = 0;
		int m          = totalcount % pagesize;
		if ( m > 0 ) {
			pagecount = totalcount / pagesize + 1;
		} else {
			pagecount = totalcount / pagesize;
		}

		for ( int i = 1; i <= pagecount; i++ ) {
			if ( m == 0 ) {
				List<String> subList = list.subList( ( i - 1 ) * pagesize, pagesize * ( i ) );
				redisUtil.hDelete( CACHE_GIVE_MONEY_MAP, subList.toArray() );
			} else {
				if ( i == pagecount ) {
					List<String> subList = list.subList( ( i - 1 ) * pagesize, totalcount );
					redisUtil.hDelete( CACHE_GIVE_MONEY_MAP, subList.toArray() );
				} else {
					List<String> subList = list.subList( ( i - 1 ) * pagesize, pagesize * ( i ) );
					redisUtil.hDelete( CACHE_GIVE_MONEY_MAP, subList.toArray() );
				}
			}
		}
	}
}
