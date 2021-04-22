package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class VideoMemberUtil {
	@Autowired
	private RedisUtil redisUtil;

	// 直播间成员 key 类型zset score为主播ID
	public static final String REDIS_MEM_KEY = Constants.LIVE_PREX + "liveVideoMember";

	/**
	 * 会员进入房间，记录观看人数
	 *
	 * @param memberId 会员ID
	 * @param roomId   房间ID(主播ID)
	 * @return 是否添加成功
	 */
	public boolean addRoomMember( String memberId, double roomId ) {
		Boolean isAdd = redisUtil.zAdd( REDIS_MEM_KEY, memberId, roomId );
		return isAdd != null && isAdd;
	}

	public Integer getRoomByMember( String memberId ) {
		Double roomId = redisUtil.zScore( REDIS_MEM_KEY, memberId );
		return roomId == null ? null : roomId.intValue();
	}

	public long countRoomMember( double roomId ) {
		Long count = redisUtil.zCount( REDIS_MEM_KEY, roomId, roomId );
		return count == null ? 1 : count + 1;
	}

	public Set<String> getRoomMember( double roomId, long offset, long count ) {
		return redisUtil.zRangeByScore( REDIS_MEM_KEY, roomId, roomId, offset, count );
	}

	public long removeRoom( double roomId ) {
		Long count = redisUtil.zRemoveRangeByScore( REDIS_MEM_KEY, roomId, roomId );
		return count == null ? 0 : count + 1;
	}

	public boolean removeRoomMember( String memberId ) {
		Long removeCount = redisUtil.zRemove( REDIS_MEM_KEY, memberId );
		return removeCount != null && removeCount > 0;
	}
}
