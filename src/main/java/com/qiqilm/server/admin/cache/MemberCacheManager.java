package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.LiveUserMount;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.WheelUser;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.LiveUserMountMapper;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.mapper.WheelUserMapper;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员数据缓存
 */
@Component
@Log4j2
public class MemberCacheManager {
	@Resource
	private MemberInfoMapper    memberInfoMapper;
	@Resource
	private WheelUserMapper     wheelUserMapper;
	@Resource
	private LiveUserMountMapper liveUserMountMapper;
	@Resource
	private RedisUtil          redisUtil;
	@Resource
	private SysConfigCacheUtil sysConfigCacheUtil;

	@Value( "${spring.profiles.active}" )
	private String profile;

	public String getAgent() {
		return profile;
	}

	@PostConstruct
	public void init() {
		initMemberCode();
	}

	public void initMemberCode() {
		int mysqlMaxCode = memberInfoMapper.selectMaxMemberCode();
		if ( mysqlMaxCode == 0 ) {
			return;
		}
		RedisAtomicLong entityIdCounter = new RedisAtomicLong( Constants.CX_MENBER_CODE,
				redisUtil.getConnectionFactory() );
		long redisMaxCode = Constants.INIT_MEMBERCODE + entityIdCounter.get();
		if ( redisMaxCode < mysqlMaxCode ) {
			entityIdCounter.set( mysqlMaxCode + 10 );
		}
	}

	//获取登录信息
	public Map<Object, Object> getMemberInfo( String token ) {
		log.debug( "token:" + token );
		if ( StringUtils.isEmpty( token ) ) {
			throw new BusinessException( "token错误" );
		}
		return redisUtil.hGetAll( Constants.TOKEN_USER_KEY + token );
	}

	//获取登录信息
	public String getMemberId( String token ) {
		log.debug( "token:" + token );
		if ( StringUtils.isEmpty( token ) ) {
			throw new BusinessException( "token错误" );
		}
		Object memberId = redisUtil.hGet( Constants.TOKEN_USER_KEY + token, "userId" );
		return memberId == null ? null : memberId.toString();
	}

	//获取登录信息
	public String getMemberId( HttpServletRequest request ) {
		String token = request.getHeader( Constants.TOKEN );
		return getMemberId( token );
	}

	//获取登录信息
	public String getTokenByUserId( String userId ) {
		return redisUtil.strGet( Constants.USER_TOKEN_KEY + userId );
	}

	//获取登录信息
	public String getMemberCode( String token ) {

		String userId = getMemberId( token );
		return getMemberCodeByUserId( userId );
	}

	//获取登录信息
	public String getMemberCodeByUserId( String userId ) {
		return userId.split( "_" )[ 1 ];
	}

	/**
	 * 删除登录token
	 *
	 * @return
	 */
	public void delToken( HttpServletRequest request ) {
		String token    = request.getHeader( Constants.TOKEN );
		String memberId = this.getMemberId( token );
		if ( StringUtils.isNotBlank( memberId ) ) {
			redisUtil.unlink( Constants.USER_TOKEN_KEY + memberId );
		}
		redisUtil.unlink( Constants.TOKEN_USER_KEY + token );
	}

	/**
	 * 删除登录token
	 *
	 * @return
	 */
	@Async
	public void delToken( String memberId ) {
		//大平台
		String token = getTokenByUserId( memberId );
		redisUtil.unlink( Constants.TOKEN_USER_KEY + token );
		if ( StringUtils.isNotBlank( memberId ) ) {
			redisUtil.unlink( Constants.USER_TOKEN_KEY + memberId );
		}
	}

	/**
	 * 生成会员编号
	 *
	 * @return
	 */
	private String makeMemberCode() {
		RedisAtomicLong entityIdCounter = new RedisAtomicLong( Constants.CX_MENBER_CODE,
				redisUtil.getConnectionFactory() );
		return String.valueOf( Constants.INIT_MEMBERCODE + entityIdCounter.getAndIncrement() );
	}

	/**
	 * 产生会员
	 */
	public MemberInfo createMember() {
		MemberInfo member = new MemberInfo();
		member.setCxAgent( getAgent() );
		member.setMemberCode( makeMemberCode() );
		member.setHeadImg( String.valueOf( RandomUtils.nextInt( 1, 7 ) ) );
		member.setId( member.getCxAgent().concat( "_" ).concat( member.getMemberCode() ) );
		return member;
	}

	public void checkFirstChargeaddWheelTimes( String pUserId ) {
		WheelUser wheelUser = wheelUserMapper.selectWheelUserById( pUserId );
		if ( wheelUser == null ) {
			wheelUser = new WheelUser();
			wheelUser.setId( pUserId );
			wheelUser.setTimes( 1 );
			wheelUser.setSkinTimes( 1 );
			wheelUserMapper.insertWheelUser( wheelUser );

		} else {
			wheelUser.setTimes( 1 );
			wheelUser.setSkinTimes( 1 );
			wheelUserMapper.updateWheelUser( wheelUser );
		}
		int mountId = sysConfigCacheUtil.getConfInt( "bank_mount" );
		if ( mountId == 0 ) {
			return;
		}
		LiveUserMount query = new LiveUserMount();
		query.setUserId( pUserId );
		query.setMountId( mountId );
		int day = 3;
		if ( DateFormatUtils.getDaysOfHour( new Date() ) > 12 ) {
			day += 1;
		}
		List<LiveUserMount> list = liveUserMountMapper.selectLiveUserMountList( query );
		if ( list.isEmpty() ) {
			query.setIsUse( "0" );
			Date d = new Date( System.currentTimeMillis() + day * 24 * 60 * 60 * 1000L );//过期时间
			query.setEffectiveTime( d );
			liveUserMountMapper.insertLiveUserMount( query );
		} else {
			LiveUserMount db = list.get( 0 );
			if ( db.getEffectiveTime().getTime() > System.currentTimeMillis() ) {
				db.setEffectiveTime( new Date( db.getEffectiveTime().getTime() + day * 24 * 60 * 60 * 1000L ) );
			} else {
				Date d = new Date( System.currentTimeMillis() + day * 24 * 60 * 60 * 1000L );//过期时间
				db.setEffectiveTime( d );
			}

			liveUserMountMapper.updateLiveUserMount( db );
		}
	}

	public void bankChargeMount( String pUserId ) {
		int mountId = sysConfigCacheUtil.getConfInt( "bank_mount" );
		if ( mountId == 0 ) {
			return;
		}
		//加坐骑33
		LiveUserMount query = new LiveUserMount();
		query.setUserId( pUserId );
		query.setMountId( mountId );
		int day = 1;
		if ( DateFormatUtils.getDaysOfHour( new Date() ) > 12 ) {
			day += 1;
		}

		List<LiveUserMount> list = liveUserMountMapper.selectLiveUserMountList( query );
		if ( list.size() == 0 ) {
			query.setIsUse( "0" );
			Date d = new Date( System.currentTimeMillis() + day * 24 * 60 * 60 * 1000L );//过期时间
			query.setEffectiveTime( d );
			liveUserMountMapper.insertLiveUserMount( query );
		} else {
			LiveUserMount db = list.get( 0 );
			if ( db.getEffectiveTime().getTime() > System.currentTimeMillis() ) {
				db.setEffectiveTime( new Date( db.getEffectiveTime().getTime() + day * 24 * 60 * 60 * 1000L ) );
			} else {
				Date d = new Date( System.currentTimeMillis() + day * 24 * 60 * 60 * 1000L );//过期时间
				db.setEffectiveTime( d );
			}
			liveUserMountMapper.updateLiveUserMount( db );
		}
	}
}
