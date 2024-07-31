package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.SysUser;
import com.qiqilm.server.admin.enums.UserStatus;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户验证处理
 *
 * @author 77tv
 */
@Log4j2
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private ISysUserService      userService;
	@Autowired
	private SysPermissionService permissionService;

	@Override
	public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
		SysUser user = userService.selectUserByUserName( username );
		if ( StringUtils.isNull( user ) ) {
			log.info( "登录用户：{} 不存在.", username );
			throw new UsernameNotFoundException( "登录用户：" + username + " 不存在" );
		} else if ( UserStatus.DELETED.getCode().equals( user.getDelFlag() ) ) {
			log.info( "登录用户：{} 已被删除.", username );
			throw new BusinessException( "对不起，您的账号：" + username + " 已被删除" );
		} else if ( UserStatus.DISABLE.getCode().equals( user.getStatus() ) ) {
			log.info( "登录用户：{} 已被停用.", username );
			throw new BusinessException( "对不起，您的账号：" + username + " 已停用" );
		}

		return createLoginUser( user );
	}

	public UserDetails createLoginUser( SysUser user ) {
		return new LoginUser( user, permissionService.getMenuPermission( user ) );
	}
}
