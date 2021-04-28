package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.rsp.RspVideoClassified;
import com.qiqilm.server.admin.mapper.LiveVideoClassifiedMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoClassifiedListCacheUtil {

	private static final String CLASSIFIED_LIST = Constants.LIVE_PREX + "classified:listStr";

	@Autowired
	private LiveVideoClassifiedMapper liveVideoClassifiedMapper;
	@Autowired
	private RedisUtil                 redisUtil;

	public void refreshKey() {
		List<RspVideoClassified> classfyList = liveVideoClassifiedMapper.getClassfyList();
		redisUtil.unlink( CLASSIFIED_LIST );
		redisUtil.strSet( CLASSIFIED_LIST, JsonUtil.object2Json( classfyList ) );
	}
}
