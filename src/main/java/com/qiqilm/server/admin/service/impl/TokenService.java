package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.constant.AdminConstants;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.utils.*;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * token验证处理
 *
 * @author 77tv
 */
@Component
public class TokenService {
	protected static final long MILLIS_SECOND     = 1000L;
	protected static final long MILLIS_MINUTE     = 2 * 60 * MILLIS_SECOND;
	private static final   Long MILLIS_MINUTE_SIX = 60 * 60 * 1000L;

	// 令牌自定义标识
	@Value( "${token.header}" )
	private String header;
	// 令牌秘钥
	@Value( "${token.secret}" )
	private String secret;
	// 令牌有效期（默认30分钟）
	@Value( "${token.expireTime}" )
	private int    expireTime;

	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 获取用户身份信息
	 *
	 * @return 用户信息
	 */
	public LoginUser getLoginUser( HttpServletRequest request ) {
		// 获取请求携带的令牌
		String token = getToken( request );
		if ( StringUtils.isNotEmpty( token ) ) {
			Claims claims = parseToken( token );
			// 解析对应的权限以及用户信息
			String uuid    = ( String ) claims.get( AdminConstants.LOGIN_USER_KEY );
			String userKey = getTokenKey( uuid );
			String userStr = redisUtil.strGet( userKey );
			return JsonUtil.json2Object( userStr, LoginUser.class );
		}
		return null;
	}

	/**
	 * 设置用户身份信息
	 */
	public void setLoginUser( LoginUser loginUser ) {
		if ( StringUtils.isNotNull( loginUser ) && StringUtils.isNotEmpty( loginUser.getToken() ) ) {
			refreshToken( loginUser );
		}
	}

	/**
	 * 删除用户身份信息
	 */
	public void delLoginUser( LoginUser loginUser ) {
		if ( StringUtils.isNotEmpty( loginUser.getToken() ) ) {
			String tokenKey = getTokenKey( loginUser.getToken() );
			String userKey  = getUserKey( loginUser.getUser().getUserId() );
			redisUtil.unlink( Arrays.asList( tokenKey, userKey ) );
		}
	}

	/**
	 * 创建令牌
	 *
	 * @param loginUser 用户信息
	 * @return 令牌
	 */
	public String createToken( LoginUser loginUser ) {
		String token = UuidUtil.getRandomUuidWithoutSeparator();
		loginUser.setToken( token );
		setUserAgent( loginUser );
		refreshToken( loginUser );

		Map<String, Object> claims = new HashMap<>();
		claims.put( AdminConstants.LOGIN_USER_KEY, token );
		return createToken( claims );
	}

	/**
	 * 验证令牌有效期，相差不足1小时，自动刷新缓存
	 *
	 * @return 令牌
	 */
	public void verifyToken( LoginUser loginUser ) {
		long expireTime  = loginUser.getExpireTime();
		long currentTime = System.currentTimeMillis();
		if ( expireTime - currentTime <= MILLIS_MINUTE_SIX ) {
			refreshToken( loginUser );
		}
	}

	/**
	 * 刷新令牌有效期
	 *
	 * @param loginUser 登录信息
	 */
	public void refreshToken( LoginUser loginUser ) {
		loginUser.setLoginTime( System.currentTimeMillis() );
		loginUser.setExpireTime( loginUser.getLoginTime() + expireTime * MILLIS_MINUTE );

		String userKey  = getUserKey( loginUser.getUser().getUserId() );
		String oldToken = redisUtil.strGet( userKey );
		if ( StringUtils.isNotEmpty( oldToken ) ) {
			String oldTokenKey = getTokenKey( oldToken );
			redisUtil.unlink( oldTokenKey );
		}

		// 根据uuid将loginUser缓存
		String tokenKey = getTokenKey( loginUser.getToken() );
		redisUtil.strSet( tokenKey, JsonUtil.object2Json( loginUser ), Duration.ofMinutes( expireTime ) );
		redisUtil.strSet( userKey, loginUser.getToken(), Duration.ofMinutes( expireTime ) );
	}

	/**
	 * 设置用户代理信息
	 *
	 * @param loginUser 登录信息
	 */
	public void setUserAgent( LoginUser loginUser ) {
		UserAgent userAgent = UserAgent.parseUserAgentString( ServletUtil.getHttpServletRequest().getHeader( "User-Agent" ) );
		String    ip        = UserDataUtil.getIp( ServletUtil.getHttpServletRequest() );
		loginUser.setIpaddr( ip );
		// loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
		loginUser.setBrowser( userAgent.getBrowser().getName() );
		loginUser.setOs( userAgent.getOperatingSystem().getName() );
	}

	/**
	 * 从数据声明生成令牌
	 *
	 * @param claims 数据声明
	 * @return 令牌
	 */
	private String createToken( Map<String, Object> claims ) {
		return Jwts.builder()
				.setClaims( claims )
				.signWith( SignatureAlgorithm.HS512, secret ).compact();
	}

	/**
	 * 从令牌中获取数据声明
	 *
	 * @param token 令牌
	 * @return 数据声明
	 */
	private Claims parseToken( String token ) {
		return Jwts.parser()
				.setSigningKey( secret )
				.parseClaimsJws( token )
				.getBody();
	}

	/**
	 * 从令牌中获取用户名
	 *
	 * @param token 令牌
	 * @return 用户名
	 */
	public String getUsernameFromToken( String token ) {
		Claims claims = parseToken( token );
		return claims.getSubject();
	}

	/**
	 * 获取请求token
	 *
	 * @return token
	 */
	private String getToken( HttpServletRequest request ) {
		String token = request.getHeader( header );
		if ( StringUtils.isNotEmpty( token ) && token.startsWith( AdminConstants.TOKEN_PREFIX ) ) {
			token = token.replace( AdminConstants.TOKEN_PREFIX, "" );
		}
		return token;
	}

	private String getTokenKey( String uuid ) {
		return AdminConstants.LOGIN_TOKEN_KEY + uuid;
	}

	private String getUserKey( Long userId ) {
		return AdminConstants.LOGIN_USER_TOEN_KEY + userId;
	}
}
