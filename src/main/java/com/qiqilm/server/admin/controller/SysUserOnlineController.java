package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.constant.AdminConstants;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.SysUserOnline;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ISysUserOnlineService;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

	@PreAuthorize( "@ss.hasPermi('monitor:online:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( String ipaddr, String userName ) {
		Map<String, LoginUser> loginUserList = stringRedisTemplate.execute( ( RedisCallback<Map<String, LoginUser>> )
				connection -> {
					Map<String, LoginUser> resultMap = new HashMap<>();
					Cursor<byte[]> cursor = connection.scan( ScanOptions.scanOptions()
							.match( AdminConstants.LOGIN_TOKEN_KEY + "*" ).count( 3 ).build() );
					while ( cursor.hasNext() ) {
						String    key  = new String( cursor.next() );
						LoginUser user = JsonUtil.json2Object( stringRedisTemplate.opsForValue().get( key ), LoginUser.class );
						resultMap.put( user.getToken(), user );
					}
					return resultMap;
				} );
		List<SysUserOnline> userOnlineList = new ArrayList<>();
		if ( !CollectionUtils.isEmpty( loginUserList ) ) {
			for ( LoginUser user : loginUserList.values() ) {
				if ( StringUtils.isNotEmpty( ipaddr ) && StringUtils.isNotEmpty( userName ) ) {
					if ( StringUtils.equals( ipaddr, user.getIpaddr() ) && StringUtils.equals( userName, user.getUsername() ) ) {
						userOnlineList.add( userOnlineService.selectOnlineByInfo( ipaddr, userName, user ) );
					}
				} else if ( StringUtils.isNotEmpty( ipaddr ) ) {
					if ( StringUtils.equals( ipaddr, user.getIpaddr() ) ) {
						userOnlineList.add( userOnlineService.selectOnlineByIpaddr( ipaddr, user ) );
					}
				} else if ( StringUtils.isNotEmpty( userName ) ) {
					if ( StringUtils.equals( userName, user.getUsername() ) ) {
						userOnlineList.add( userOnlineService.selectOnlineByUserName( userName, user ) );
					}
				} else {
					userOnlineList.add( userOnlineService.loginUserToUserOnline( user ) );
				}
			}
		}
		userOnlineList.removeAll( Collections.singleton( null ) );
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
		stringRedisTemplate.unlink( AdminConstants.LOGIN_TOKEN_KEY + tokenId );
		return AjaxResult.success();
	}
}
