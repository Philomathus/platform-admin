package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.constant.ConstantsWeb;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 会员数据缓存
 */
@Component
@Log4j2
public class MemberCacheManager {
	@Resource
    private MemberInfoMapper memberInfoMapper;
	@Autowired
	private RedisUtil redisUtil;

	public void init() {
		initMemberCode();
		//initWebSet();
	}

	public void initMemberCode() {
		int mysqlMaxCode = memberInfoMapper.selectMaxMemberCode();
		if ( mysqlMaxCode == 0 ) {
			return;
		}
		RedisAtomicLong entityIdCounter = new RedisAtomicLong( Constants.CX_MENBER_CODE,
                redisUtil.getConnectionFactory() );
		long            redisMaxCode    = Constants.INIT_MEMBERCODE + entityIdCounter.get();
		if ( redisMaxCode < mysqlMaxCode ) {
			entityIdCounter.set( mysqlMaxCode + 10 );
		}
	}


	public void addWebSetVal( String key, String val ) {
        redisUtil.strSet( Constants.CX_WEB_SET.concat( key ), val );
	}

	public String getWebSetVal( String key ) {
		return redisUtil.strGet( Constants.CX_WEB_SET.concat( key ) );
	}

//	public void initWebSet() {
//		log.info( "初始化平台设置开始" );
//		for ( RspWebSet set : configWebMapper.selectAll() ) {
//			addWebSetVal( set.getKey_id(), set.getVal() );
//			log.info( "{}：{}={}", set.getDes(), set.getKey_id(), set.getVal() );
//		}
//		log.info( "初始化平台设置结束" );
//	}

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
	public void delToken(String memberId) {
		//大平台
		String token = getTokenByUserId(memberId);
		redisUtil.unlink( Constants.TOKEN_USER_KEY + token );
		if(StringUtils.isNotBlank( memberId )){
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
		member.setCxAgent( getWebSetVal( ConstantsWeb.agent_id ) );
		member.setMemberCode( makeMemberCode() );
		member.setHeadImg( String.valueOf( RandomUtils.nextInt( 1, 7 ) ) );
		member.setId( member.getCxAgent().concat( "_" ).concat( member.getMemberCode() ) );
		return member;
	}
}
