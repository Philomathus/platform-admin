package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.constant.AdminConstants;
import com.qiqilm.server.admin.core.factory.AsyncFactory;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginBody;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.utils.AsyncManager;
import com.qiqilm.server.admin.utils.MessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 登录校验方法
 *
 * @author 77tv
 */
@Log4j2
@Component
public class SysLoginService {
	@Autowired
	private TokenService          tokenService;
	@Resource
	private AuthenticationManager authenticationManager;
	@Autowired
	private ISysUserService       userService;
	//	@Autowired
	//	private SystemIpWhiteMapper   systemIpWhiteMapper;

	/**
	 * 登录验证
	 *
	 * @param ip        登录IP
	 * @param loginBody 登录信息
	 * @return 结果
	 */
	public AjaxResult login( String ip, LoginBody loginBody ) throws Exception {
		String googleAuthSecret = userService.selectGoogleAuthKeyByUserName( loginBody.getUsername() );
/*
		if ( StringUtils.isBlank( googleAuthSecret ) ) {
			return AjaxResult.error( "请联系管理员绑定google验证秘钥" );
		}
		String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr(
				"secretkey/googleAuthPrivateKey" ) );
		if ( !GoogleAuthUtil.verifyCode( googleAuthKey, loginBody.getGoogleAuthCode() ) ) {
			AsyncManager.me().execute( AsyncFactory.recordLogininfor( loginBody.getUsername(), AdminConstants.LOGIN_FAIL,
					MessageUtils.message( "user.google.auth.error" ) ) );
			return AjaxResult.error( "google验证码不正确，请检查" );
		}
*/

		// 用户验证
		Authentication authentication = null;
		try {
			// 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken( loginBody.getUsername(), loginBody.getPassword() ) );
		} catch ( Exception e ) {
			if ( e instanceof BadCredentialsException ) {
				String message = MessageUtils.message( "user.password.not.match" );
				AsyncManager.me().execute( AsyncFactory.recordLogininfor( loginBody.getUsername(), AdminConstants.LOGIN_FAIL,
						message ) );
				return AjaxResult.error( message );
			} else {
				AsyncManager.me().execute( AsyncFactory.recordLogininfor( loginBody.getUsername(), AdminConstants.LOGIN_FAIL,
						e.getMessage() ) );
				return AjaxResult.error( e.getMessage() );
			}
		}

		//		log.info( "管理员{}登录IP:{}", loginBody.getUsername(), ip );
		//		String ipId = systemIpWhiteMapper.selectEffectIp( ip );
		//		if ( StringUtils.isBlank( ipId ) ) {
		//			AsyncManager.me().execute( AsyncFactory.recordLogininfor( loginBody.getUsername(), AdminConstants.LOGIN_FAIL,
		//					MessageUtils.message( "user.block.ip" ), ip ) );
		//			log.warn( "限制IP:{}登录", ip );
		//			return AjaxResult.error( "您所在区域无法登录本系统IP：" + ip );
		//		}

		AsyncManager.me().execute( AsyncFactory.recordLogininfor( loginBody.getUsername(), AdminConstants.LOGIN_SUCCESS,
				MessageUtils.message( "user.login.success" ) ) );
		LoginUser loginUser = ( LoginUser ) authentication.getPrincipal();
		// 生成token
		String     token = tokenService.createToken( loginUser );
		AjaxResult ajax  = AjaxResult.success();
		ajax.put( AdminConstants.TOKEN, token );
		return ajax;
	}
}
