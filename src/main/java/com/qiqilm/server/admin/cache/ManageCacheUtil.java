package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.mapper.LiveBlackMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class ManageCacheUtil {
	@Autowired
	private RedisUtil redisUtil;
	@Resource
	private LiveBlackMapper liveBlackMapper;
	public static final String            LIVE_BLACK_KEY = Constants.LIVE_PREX + "liveBlack:";


	public Set refreshBlack( Long host_id ) {
		redisUtil.unlink( LIVE_BLACK_KEY + host_id );
		Set set = liveBlackMapper.userBlackList( host_id );
		redisUtil.sAdd( LIVE_BLACK_KEY + host_id, JsonUtil.object2Json( set ) );
		return set;
	}



}
