package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.mapper.LiveVideoMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class LiveVideoSortCacheUtil {
	private static final String VIDEO_SORT = Constants.LIVE_PREX + "videoSort";

	@Autowired
	private RedisUtil       redisUtil;
	@Autowired
	private LiveVideoMapper liveVideoMapper;

	public void processVideoSort() {
		List<LiveVideo> liveVideos = liveVideoMapper.selectLiveInVideoSort();

		// 固定位
		Map<Integer, Long> sortHostMap = new TreeMap<>();
		// 推荐位
		List<Long> recommendHostList = new ArrayList<>();
		// 正常位
		List<Long> normalHostList = new ArrayList<>();
		// 置底位
		List<Long> stickHostList = new ArrayList<>();

		liveVideos.forEach( liveVideo -> {
			if ( liveVideo.getSort() < 9999000 ) {
				sortHostMap.put( liveVideo.getSort().intValue(), liveVideo.getId() );
			} else if ( liveVideo.getIsRecommend() == 1 ) {
				recommendHostList.add( liveVideo.getId() );
			} else if ( liveVideo.getIsRecommend() == 0 && liveVideo.getStick() == 0 ) {
				normalHostList.add( liveVideo.getId() );
			} else if ( liveVideo.getStick() == 1 ) {
				stickHostList.add( liveVideo.getId() );
			}
		} );

		List<Long> resultList = new ArrayList<>( liveVideos.size() );
		for ( int i = 1; i <= liveVideos.size(); i++ ) {
			Long sortHostId = sortHostMap.get( i );
			if ( sortHostId != null ) {
				resultList.add( i - 1, sortHostId );
				sortHostMap.remove( i );
			} else if ( !CollectionUtils.isEmpty( recommendHostList ) ) {
				resultList.add( recommendHostList.get( 0 ) );
				recommendHostList.remove( 0 );
			} else if ( !CollectionUtils.isEmpty( normalHostList ) ) {
				resultList.add( normalHostList.get( 0 ) );
				normalHostList.remove( 0 );
			}
		}

		if ( !CollectionUtils.isEmpty( sortHostMap ) ) {
			resultList.addAll( sortHostMap.values() );
		}
		if ( !CollectionUtils.isEmpty( stickHostList ) ) {
			resultList.addAll( stickHostList );
		}

		Set<ZSetOperations.TypedTuple<Integer>> tupless = new HashSet<>();
		for ( int i = 0; i < resultList.size(); i++ ) {
			ZSetOperations.TypedTuple<Integer> objectTypedTuple1 = new DefaultTypedTuple<>( resultList.get( i ).toString(), i );
			tupless.add( objectTypedTuple1 );
		}

		redisUtil.zAddAll( VIDEO_SORT, tupless )
	}
}
