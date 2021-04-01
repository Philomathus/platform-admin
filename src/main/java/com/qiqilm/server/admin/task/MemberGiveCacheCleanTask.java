package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class MemberGiveCacheCleanTask {
	@Autowired
	private RedisUtil redisUtil;

	public static final String CACHE_GIVE_MONEY_MAP = Constants.LIVE_PREX + "user:giveMoney";


}
