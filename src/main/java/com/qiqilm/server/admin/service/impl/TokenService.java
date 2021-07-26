package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.constant.AdminConstants;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.utils.*;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * token验证处理
 *
 * @author 77tv
 */
@Log4j2
@Component
public class TokenService {
	protected static final long MILLIS_SECOND = 1000L;
	protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
	private static final   Long MILLIS_HOUR   = 60 * MILLIS_MINUTE;

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

	@Autowired
    private StringRedisTemplate stringRedisTemplate;

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
//            Map tokenKeys = (Map)redisUtil.hGet("tokenKeys", userKey);
            Object tokenKeysString = redisUtil.hGet("tokenKeys", userKey);
            if (tokenKeysString!= null){
                Map tokenKeys = JsonUtil.json2Object((String) tokenKeysString, Map.class);
                long expireDate = (long)tokenKeys.get("expireDate");
                if (System.currentTimeMillis()<= expireDate) {
                    JsonUtil.json2Object( (String)tokenKeys.get("loginUser"), LoginUser.class );
                    return JsonUtil.json2Object( (String)tokenKeys.get("loginUser"), LoginUser.class );
                }
            }

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
/*			String tokenKey = getTokenKey( loginUser.getToken() );
			String userKey  = getUserKey( loginUser.getUser().getUserId() );
			redisUtil.unlink( Arrays.asList( tokenKey, userKey ) );*/
            String tokenKey = TokenService.getTokenKey(loginUser.getToken());
            stringRedisTemplate.opsForHash().delete( "tokenKeys", tokenKey );
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
		if ( ( expireTime - currentTime ) <= MILLIS_HOUR ) {
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
		loginUser.setExpireTime( loginUser.getLoginTime() + ( expireTime * MILLIS_MINUTE ) );

		String userKey  = getUserKey( loginUser.getUser().getUserId() );
		String oldToken = redisUtil.strGet( userKey );
		if ( StringUtils.isNotEmpty( oldToken ) ) {
			String oldTokenKey = getTokenKey( oldToken );
			redisUtil.unlink( oldTokenKey );
		}

		// 根据uuid将loginUser缓存
		String tokenKey = getTokenKey( loginUser.getToken() );
//		redisUtil.strSet( tokenKey, JsonUtil.object2Json( loginUser ), Duration.ofMinutes( expireTime ) );
		//获取截止时间
        long expireDate = System.currentTimeMillis() + Duration.ofMinutes(expireTime).toMillis();
        Map<String, Object> map = new HashMap<>();
        //登录前先清除之前的登录的token
        Object userKeys = redisUtil.hGet("userKeys", userKey);
        if (userKeys!=null) {
            String tokenId = (String)(JsonUtil.json2Map((String) userKeys)).get("userKey");
            String tokenKeyOld = TokenService.getTokenKey(tokenId);
            stringRedisTemplate.opsForHash().delete( "tokenKeys", tokenKeyOld );
        }
        map.put("loginUser", JsonUtil.object2Json(loginUser));
        map.put("expireDate",expireDate);
        redisUtil.hSet("tokenKeys",tokenKey,JsonUtil.object2Json(map));
//		redisUtil.strSet( userKey, loginUser.getToken(), Duration.ofMinutes( expireTime ) );
        Map<String, Object> map2 = new HashMap<>();
        map2.put("userKey", loginUser.getToken());
        map2.put("expireDate",expireDate);
        redisUtil.hSet("userKeys",userKey,JsonUtil.object2Json(map2));
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

	public static String getTokenKey( String uuid ) {
		return AdminConstants.LOGIN_TOKEN_KEY + uuid;
	}

    public static String getUserKey( Long userId ) {
		return AdminConstants.LOGIN_USER_TOEN_KEY + userId;
	}
	public  void  delToken(Long userId){
		String userKey  = getUserKey( userId);
		Object userKeys = redisUtil.hGet("userKeys", userKey);
		String tokenId = null;
		if (userKeys!=null) {
			 tokenId = (String) (JsonUtil.json2Map((String) userKeys)).get("userKey");
		}
		String token = TokenService.getTokenKey(tokenId);
		redisUtil.hDelete("userKeys",userKey);
		redisUtil.hDelete("tokenKeys",token);
	}
}
