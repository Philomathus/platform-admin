package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class VideoPayCacheUtil {
	// 直播收费key 类型set
	public static final String REDIS_KEY = Constants.LIVE_PREX + "liveVideoPay:";

	@Autowired
	private RedisUtil          redisUtil;
	@Autowired
	private SysConfigCacheUtil sysConfigCacheUtil;

	public void initVideoPay( Long videoId ) {
		redisUtil.sAdd( REDIS_KEY + videoId, "0" );
		redisUtil.expire( REDIS_KEY + videoId, Duration.ofHours(
				sysConfigCacheUtil.getConfInt( "live_pay_scene_expire", 4 ) ) );
	}

	/**
	 * 添加收费用户
	 *
	 * @param videoId  直播间ID
	 * @param memberId 会员ID
	 */
	public void addVideoPayMember( Long videoId, String memberId ) {
		redisUtil.sAdd( REDIS_KEY + videoId, memberId );
		Long expire = redisUtil.getExpire( REDIS_KEY + videoId );
		if ( expire != null && expire < 0 ) {
			redisUtil.expire( REDIS_KEY + videoId, Duration.ofHours(
					sysConfigCacheUtil.getConfInt( "live_pay_scene_expire", 4 ) ) );
		}
	}

	/**
	 * 判断是否是收费用户
	 *
	 * @param videoId  直播间ID
	 * @param memberId 会员ID
	 * @return 是否是收费用户
	 */
	public boolean isVideoPayMember( Long videoId, String memberId ) {
		Boolean isMember = redisUtil.sIsMember( REDIS_KEY + videoId, memberId );
		return isMember != null && isMember;
	}

	/**
	 * 获取直播间付费人数
	 *
	 * @param videoId 直播间ID
	 * @return 直播间付费人数
	 */
	public long countVideoPayMember( Long videoId ) {
		Long size = redisUtil.sSize( REDIS_KEY + videoId );
		return size == null ? 0 : size;
	}

	/**
	 * 移除直播间付费列表
	 *
	 * @param videoId 直播间ID
	 */
	public void unlink( Long videoId ) {
		redisUtil.unlink( REDIS_KEY + videoId );
	}
}
