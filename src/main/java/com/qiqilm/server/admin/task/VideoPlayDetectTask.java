package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.mapper.LiveVideoMapper;
import com.qiqilm.server.admin.utils.HttpHelper;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频播放检测任务
 *
 * @author qicheng
 */
@Log4j2
@Component
public class VideoPlayDetectTask {
	@Resource
	private LiveVideoMapper liveVideoMapper;
	@Autowired
	private RedisUtil       redisUtil;

	/**
	 * 视频播放检测
	 */
	@Scheduled( cron = "0 0/2 * * * ?" )
	public void videoPlayDetect() {
		Boolean lock = redisUtil.strSetIfAbsent( "videoPlayDetect", "1", Duration.ofSeconds( 119 ) );
		if ( lock != null && lock ) {
			List<LiveVideo>     videoList = liveVideoMapper.selectLiveInPlayDetect();
			Map<String, String> failMap   = new HashMap<>();

			videoList.forEach( video -> this.detectVideoPlayUrl( video, failMap ) );

			redisUtil.unlink( Constants.REDIS_KEY_DETECT_PLAY );
			if ( !failMap.isEmpty() ) {
				redisUtil.hMSet( Constants.REDIS_KEY_DETECT_PLAY, failMap );
			}
		}
	}

	/**
	 * 检测直播视频推流
	 */
	private void detectVideoPlayUrl( LiveVideo video, Map<String, String> failIds ) {
		if ( StringUtils.isBlank( video.getPlayUrl() ) ) {
			return;
		}
		if ( !HttpHelper.isConnServerByHttp( video.getPlayUrl() ) ) {
			log.error( "直播间：{}，主题：{} 播放地址无效，请通知主播重新开播", video.getId(), video.getTitle() );
			failIds.put( video.getId().toString(), "0" );
		} else {
			failIds.put( video.getId().toString(), "1" );
		}
	}
}
