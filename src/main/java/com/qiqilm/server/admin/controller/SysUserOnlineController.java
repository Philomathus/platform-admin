package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ISysUserOnlineService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * 在线用户监控
 *
 * @author 77tv
 */
@Log4j2
@RestController
@RequestMapping( "/monitor/online" )
public class SysUserOnlineController extends BaseController {
	@Autowired
	private ISysUserOnlineService userOnlineService;
	@Autowired
	private StringRedisTemplate   stringRedisTemplate;
	@Autowired
    private RedisUtil redisUtil;

	@PreAuthorize( "@ss.hasPermi('monitor:online:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( String ipaddr, String userName ) {
        Map<Object, Object> tokenKeys = redisUtil.hGetAll("tokenKeys");
        Iterator<Map.Entry<Object, Object>> entries = tokenKeys.entrySet().iterator();
        ArrayList<LoginUser> loginUserList = new ArrayList<>();
        ArrayList<LoginUser> userOnlineList = new ArrayList<>();
        while(entries.hasNext()){
            Map.Entry<Object, Object> entry = entries.next();
            Object value = entry.getValue();
            Map map = JsonUtil.json2Object((String) value, Map.class);
            loginUserList.add(JsonUtil.json2Object( (String)map.get("loginUser"), LoginUser.class ));
        }
		if ( !CollectionUtils.isEmpty( loginUserList ) ) {
            loginUserList.stream().forEach(user->{				if ( StringUtils.isNotEmpty( ipaddr ) && StringUtils.isNotEmpty( userName ) ) {
                if ( StringUtils.equals( ipaddr, user.getIpaddr() ) && StringUtils.equals( userName, user.getUsername() ) ) {
                    userOnlineList.add( user);
                }
            } else if ( StringUtils.isNotEmpty( ipaddr ) ) {
                if ( StringUtils.equals( ipaddr, user.getIpaddr() ) ) {
                    userOnlineList.add( user);
                }
            } else if ( StringUtils.isNotEmpty( userName ) ) {
                if ( StringUtils.equals( userName, user.getUsername() ) ) {
                    userOnlineList.add( user );
                }
            } else {
                userOnlineList.add(user );
            }});
		}
		Collections.reverse( userOnlineList );
		return getDataTable2( userOnlineList );
	}

	/**
	 * 强退用户
	 */
	@PreAuthorize( "@ss.hasPermi('monitor:online:forceLogout')" )
	@Log( title = "在线用户", businessType = BusinessType.FORCE )
	@DeleteMapping( "/{tokenId}" )
	public AjaxResult forceLogout( @PathVariable String tokenId ) {
//		stringRedisTemplate.unlink( AdminConstants.LOGIN_TOKEN_KEY + tokenId );
        //		redisUtil.strSet( tokenKey, JsonUtil.object2Json( loginUser ), Duration.ofMinutes( expireTime ) );
        String tokenKey = TokenService.getTokenKey(tokenId);
        stringRedisTemplate.opsForHash().delete( "tokenKeys", tokenKey );
		return AjaxResult.success();
	}
}
