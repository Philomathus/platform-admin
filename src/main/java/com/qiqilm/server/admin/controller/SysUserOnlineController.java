package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.constant.AdminConstants;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.domain.SysUserOnline;
import com.qiqilm.server.admin.service.ISysUserOnlineService;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 在线用户监控
 *
 * @author 77tv
 */
@RestController
@RequestMapping( "/monitor/online" )
public class SysUserOnlineController extends BaseController {
	@Autowired
	private ISysUserOnlineService userOnlineService;

	@Autowired
	private RedisUtil redisUtil;

	@PreAuthorize( "@ss.hasPermi('monitor:online:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( String ipaddr, String userName ) {
		Collection<String>  keys           = redisUtil.keys( AdminConstants.LOGIN_TOKEN_KEY + "*" );
		List<SysUserOnline> userOnlineList = new ArrayList<>();
		for ( String key : keys ) {
			LoginUser user = JsonUtil.json2Object( redisUtil.strGet( key ), LoginUser.class );
			if ( StringUtils.isNotEmpty( ipaddr ) && StringUtils.isNotEmpty( userName ) ) {
				if ( StringUtils.equals( ipaddr, user.getIpaddr() ) && StringUtils.equals( userName, user.getUsername() ) ) {
					userOnlineList.add( userOnlineService.selectOnlineByInfo( ipaddr, userName, user ) );
				}
			} else if ( StringUtils.isNotEmpty( ipaddr ) ) {
				if ( StringUtils.equals( ipaddr, user.getIpaddr() ) ) {
					userOnlineList.add( userOnlineService.selectOnlineByIpaddr( ipaddr, user ) );
				}
			} else if ( StringUtils.isNotEmpty( userName ) && StringUtils.isNotNull( user.getUser() ) ) {
				if ( StringUtils.equals( userName, user.getUsername() ) ) {
					userOnlineList.add( userOnlineService.selectOnlineByUserName( userName, user ) );
				}
			} else {
				userOnlineList.add( userOnlineService.loginUserToUserOnline( user ) );
			}
		}
		Collections.reverse( userOnlineList );
		userOnlineList.removeAll( Collections.singleton( null ) );
		return getDataTable( userOnlineList );
	}

	/**
	 * 强退用户
	 */
	@PreAuthorize( "@ss.hasPermi('monitor:online:forceLogout')" )
	@Log( title = "在线用户", businessType = BusinessType.FORCE )
	@DeleteMapping( "/{tokenId}" )
	public AjaxResult forceLogout( @PathVariable String tokenId ) {
		redisUtil.unlink( AdminConstants.LOGIN_TOKEN_KEY + tokenId );
		return AjaxResult.success();
	}
}
