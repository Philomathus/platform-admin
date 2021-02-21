package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginBody;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.SysMenu;
import com.qiqilm.server.admin.domain.SysUser;
import com.qiqilm.server.admin.service.ISysMenuService;
import com.qiqilm.server.admin.service.impl.SysLoginService;
import com.qiqilm.server.admin.service.impl.SysPermissionService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 登录验证
 *
 * @author 77tv
 */
@RestController
public class SysLoginController {
	@Autowired
	private SysLoginService       loginService;
	@Autowired
	private ISysMenuService       menuService;
	@Autowired
	private SysPermissionService  permissionService;
	@Autowired
	private TokenService          tokenService;
	@Autowired
	private ConfigDomainCacheUtil configDomainCacheUtil;

	/**
	 * 登录方法
	 *
	 * @param data 登录加密信息
	 * @return 结果
	 */
	@PostMapping( "/login" )
	public AjaxResult login( @RequestBody String data ) throws Exception {

		String ip = UserDataUtil.getIp( ServletUtil.getHttpServletRequest() );

		String    decryptStr = RSACoder.decryptByPrivateKey( data, AuthUtil.getSecurityKeyStr( "secretkey/loginPrivateKey" ) );
		LoginBody loginBody  = JsonUtil.json2Object( decryptStr, LoginBody.class );
		// 生成令牌
		return loginService.login( ip, loginBody );
	}

	/**
	 * 获取用户信息
	 *
	 * @return 用户信息
	 */
	@GetMapping( "getInfo" )
	public AjaxResult getInfo() {
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		SysUser   user      = loginUser.getUser();
		// 角色集合
		Set<String> roles = permissionService.getRolePermission( user );
		// 权限集合
		Set<String> permissions = permissionService.getMenuPermission( user );
		AjaxResult  ajax        = AjaxResult.success();
		user.setPassword( null );
		ajax.put( "user", user );
		ajax.put( "roles", roles );
		ajax.put( "permissions", permissions );
		return ajax;
	}

	/**
	 * 获取路由信息
	 *
	 * @return 路由信息
	 */
	@GetMapping( "getRouters" )
	public AjaxResult getRouters() {
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		// 用户信息
		SysUser       user   = loginUser.getUser();
		List<SysMenu> menus  = menuService.selectMenuTreeByUserId( user.getUserId() );
		AjaxResult    result = AjaxResult.success( menuService.buildMenus( menus ) );
		result.put( "vhostUrl", configDomainCacheUtil.getValue( "domain.oss" ));
		return result;
	}
}
