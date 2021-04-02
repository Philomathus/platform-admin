package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class MemberGiveCacheCleanTask {
	public static final String CACHE_GIVE_MONEY_MAP = Constants.LIVE_PREX + "user:giveMoney";

	@Autowired
	private RedisUtil        redisUtil;
	@Autowired
	private MemberInfoMapper memberInfoMapper;

	// 每天凌晨执行清理任务
	@Scheduled( cron = "0 0 * * * ?" )
	public void cleanGiveCache() {

	}
}
