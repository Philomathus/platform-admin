package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.mapper.LiveVideoMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class LiveVideoSortCacheUtil {
	private static final String VIDEO_SORT = Constants.CONFIG_PREX + "videoSort:";

	@Autowired
	private RedisUtil       redisUtil;
	@Autowired
	private LiveVideoMapper liveVideoMapper;

	public void processVideoSort() {
		List<LiveVideo> liveVideos = liveVideoMapper.selectLiveInVideoSort();

		// 固定位
		List<Long> sortHostList = new ArrayList<>();
		// 推荐位
		List<Long> recommendHostList = new ArrayList<>();
		// 正常位
		List<Long> normalHostList = new ArrayList<>();
		// 置底位
		List<Long> stickHostList = new ArrayList<>();

		liveVideos.forEach( liveVideo -> {
			if ( liveVideo.getSort() < 9999000 ) {
				sortHostList.add( liveVideo.getId() );
			} else if ( liveVideo.getIsRecommend() == 1 ) {
				recommendHostList.add( liveVideo.getId() );
			} else if ( liveVideo.getIsRecommend() == 0 && liveVideo.getStick() == 0 ) {
				normalHostList.add( liveVideo.getId() );
			} else if ( liveVideo.getStick() == 1 ) {
				stickHostList.add( liveVideo.getId() );
			}
		} );

		List<Long> resultList = new ArrayList<>(liveVideos.size());
		for ( int i = 0; i < liveVideos.size(); i++ ) {
		}
	}
}
