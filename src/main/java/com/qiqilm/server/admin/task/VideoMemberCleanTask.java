package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.VideoMemberUtil;
import com.qiqilm.server.admin.mapper.LiveVideoMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;

/**
 * 直播间人数清理任务
 *
 * @author qicheng
 */
@Log4j2
@Component
public class VideoMemberCleanTask {
	@Resource
	private LiveVideoMapper liveVideoMapper;
	@Autowired
	private VideoMemberUtil videoMemberUtil;
	@Autowired
	private RedisUtil       redisUtil;

	/**
	 * 视频播放检测
	 */
	@Scheduled( fixedDelay = 3600000, initialDelay = 10000 )
	public void videoMemberClean() {
		Boolean lock = redisUtil.strSetIfAbsent( "videoMemberClean", "1", Duration.ofMinutes( 59 ) );
		if ( lock != null && lock ) {
			List<Long> videoIds = liveVideoMapper.selectExpiredVideo();
			for ( Long videoId : videoIds ) {
				long num = videoMemberUtil.removeRoom( videoId );
				log.warn( "直播间人数统计清理 - 直播间ID：{},直播间人数数量：{}", videoId, num );
			}
		}
	}
}
