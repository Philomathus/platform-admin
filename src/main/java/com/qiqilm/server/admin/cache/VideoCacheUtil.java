package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class VideoCacheUtil {
	@Autowired
	private RedisUtil redisUtil;

	// 直播间key 类型hash
	public static final String REDIS_KEY   = Constants.LIVE_PREX + "liveVideo:";
	// 直播间monitorTime key 类型zset score为时间戳
	public static final String REDIS_M_KEY = Constants.LIVE_PREX + "liveVideoMT";

	@Getter
	public enum VideoKey {
		/**
		 * 直播间缓存KEY
		 */
		watchNumber( "watchNumber", "当前实时观看人数" ),
		virtualWatchNumber( "virtualWatchNumber", "当前虚拟观看人数" ),
		maxWatchNumber( "maxWatchNumber", "最大观看人数" ),
		voteNumber( "voteNumber", "获得礼物票（热度）" ),
		groupId( "groupId", "群组ID" ),
		monitorTime( "monitorTime", "最后心跳监听时间" ),
		livePayCount( "livePayCount", "付费观看人数" ),
		;

		private String key;
		private String des;

		VideoKey( String key, String des ) {
			this.key = key;
			this.des = des;
		}
	}

	/**
	 * 获取指定直播间所有缓存信息Map
	 *
	 * @param videoId 直播间ID
	 * @return 指定直播间所有缓存信息Map
	 */
	public Map<Object, Object> getVideoCache( int videoId ) {
		return redisUtil.hGetAll( REDIS_KEY + videoId );
	}

	/**
	 * 获取指定直播间指定某个VideoKey的值
	 *
	 * @param videoId  直播间ID
	 * @param videoKey VideoKey
	 * @return 指定直播间指定某个VideoKey的值
	 */
	public Object getVideoCache( int videoId, VideoKey videoKey ) {
		return redisUtil.hGet( REDIS_KEY + videoId, videoKey.getKey() );
	}

	/**
	 * 获取直播印票
	 *
	 * @param videoId 直播间ID
	 * @return 直播印票
	 */
	public BigDecimal getVoteNumber( int videoId ) {
		Object voteNumberObj = redisUtil.hGet( REDIS_KEY + videoId, VideoKey.voteNumber.getKey() );
		return voteNumberObj == null ? null : new BigDecimal( voteNumberObj.toString() ).setScale( 2, BigDecimal.ROUND_HALF_UP );
	}

	/**
	 * 判断指定直播间缓存是否存在
	 *
	 * @param videoId 直播间ID
	 * @return 布尔值
	 */
	public boolean existsVideoCache( int videoId ) {
		Boolean exists = redisUtil.exists( REDIS_KEY + videoId );
		return exists != null && exists;
	}

	/**
	 * 更新覆盖直播间观看人数相关信息
	 *
	 * @param videoId            直播间ID
	 * @param watchNumber        当前实时观看人数
	 * @param maxWatchNumber     最大观看人数
	 * @param virtualWatchNumber 当前虚拟观看人数
	 */
	public void updateVideoWatch( int videoId, long watchNumber, long maxWatchNumber, int virtualWatchNumber ) {
		String key = REDIS_KEY + videoId;
		redisUtil.hSet( key, VideoKey.watchNumber.getKey(), watchNumber + "" );
		redisUtil.hSet( key, VideoKey.maxWatchNumber.getKey(), maxWatchNumber + "" );
		redisUtil.hSet( key, VideoKey.virtualWatchNumber.getKey(), virtualWatchNumber + "" );
	}

	/**
	 * 增加直播间热度
	 *
	 * @param videoId    直播间ID
	 * @param voteNumber 直播间礼物数（热度），不能为负值
	 * @return 更新后的值
	 */
	public Double incVideoVoteNum( int videoId, BigDecimal voteNumber ) {
		if ( voteNumber.compareTo( BigDecimal.ZERO ) < 0 ) {
			throw new BusinessException( "您无权删减主播热度" );
		}
		return redisUtil.hIncrement( REDIS_KEY + videoId, VideoKey.voteNumber.getKey(), voteNumber.doubleValue() );
	}

	/**
	 * 增加直播间付费观看人数，每次加1
	 *
	 * @param videoId 直播间ID
	 * @return 更新后的付费观看人数
	 */
	public Long incVideoPayCount( int videoId ) {
		return redisUtil.hIncrement( REDIS_KEY + videoId, VideoKey.livePayCount.getKey(), 1 );
	}

	public Long getVideoPayCount( int videoId ) {
		Object payCount = redisUtil.hGet( REDIS_KEY + videoId, VideoKey.livePayCount.getKey() );
		return payCount == null ? 0 : Long.parseLong( payCount.toString() );
	}

	/**
	 * 添加或更新主播直播心跳时间
	 *
	 * @param videoId 直播间ID
	 * @return 是否添加或更新成功
	 */
	public Boolean updateVideoMonitorTime( Integer videoId ) {
		return redisUtil.zAdd( REDIS_M_KEY, videoId.toString(), System.currentTimeMillis() );
	}

	/**
	 * 获取一分钟前所有未刷新心跳时间的主播直播间ID列表
	 * <p>心跳接口20秒一次，如果一分钟内时间还没有更新，说明主播异常下播了</p>
	 *
	 * @return 所有一分钟内未刷新心跳时间的直播间ID
	 */
	public Set<String> getAbortVideoByMonitorTime() {
		return redisUtil.zRangeByScore( REDIS_M_KEY, 0, System.currentTimeMillis() - 300000L );
	}

	/**
	 * 获取缓存并设置直播信息
	 *
	 * @param liveVideo 需要设置的直播信息对象
	 */
	public void setVideoCache( LiveVideo liveVideo ) {
		Integer id=Integer.parseInt(""+ liveVideo.getId());
		Map<Object, Object> videoCacheMap = this.getVideoCache(id);
		if ( !CollectionUtils.isEmpty( videoCacheMap ) ) {
			String virtualWatchNumber = ( String ) videoCacheMap.getOrDefault(
					VideoCacheUtil.VideoKey.virtualWatchNumber.getKey(), "0" );
			liveVideo.setVirtualWatchNumber( Long.parseLong( virtualWatchNumber ) );

			String watchNumber = ( String ) videoCacheMap.getOrDefault(
					VideoCacheUtil.VideoKey.watchNumber.getKey(), "0" );
			liveVideo.setWatchNumber( Long.parseLong( watchNumber ) );

			String maxWatchNumber = ( String ) videoCacheMap.getOrDefault(
					VideoCacheUtil.VideoKey.maxWatchNumber.getKey(), "0" );
			liveVideo.setMaxWatchNumber( Long.parseLong( maxWatchNumber ) );

			String voteNumber = ( String ) videoCacheMap.getOrDefault(
					VideoCacheUtil.VideoKey.voteNumber.getKey(), "0" );
			liveVideo.setVoteNumber( new BigDecimal( voteNumber ).setScale( 2, BigDecimal.ROUND_HALF_UP ) );

			String livePayCount = ( String ) videoCacheMap.getOrDefault(
					VideoCacheUtil.VideoKey.livePayCount.getKey(), "0" );
			liveVideo.setLivePayCount( Long.parseLong( livePayCount ) );

			Double videoMonitorTime = this.getVideoMonitorTime(id);
			if ( videoMonitorTime != null ) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis( videoMonitorTime.longValue() );
				liveVideo.setMonitorTime( calendar.getTime() );
			}
		}
	}

	public void putHostGroupId( int videoId,String groupId ) {
		redisUtil.hSet(REDIS_KEY.concat(String.valueOf(videoId)) ,"groupId",groupId);
	}

	/**
	 * 获取指定直播间心跳监控时间
	 *
	 * @param videoId 直播间ID
	 * @return 直播间心跳监控时间
	 */
	public Double getVideoMonitorTime( Integer videoId ) {
		return redisUtil.zScore( REDIS_M_KEY, videoId.toString() );
	}

	public Long clearVideoMonitorTime( Integer videoId ) {
		return redisUtil.zRemove( REDIS_M_KEY, videoId.toString() );
	}
}
