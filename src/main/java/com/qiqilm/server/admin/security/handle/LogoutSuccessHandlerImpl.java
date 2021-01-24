package com.qiqilm.server.admin.security.handle;

import com.qiqilm.server.admin.constant.AdminConstants;
import com.qiqilm.server.admin.constant.HttpStatus;
import com.qiqilm.server.admin.core.factory.AsyncFactory;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.AsyncManager;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义退出处理类 返回成功
 *
 * @author 77tv
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
	@Autowired
	private TokenService tokenService;

	/**
	 * 退出处理
	 *
	 * @return
	 */
	@Override
	public void onLogoutSuccess( HttpServletRequest request, HttpServletResponse response, Authentication authentication )
			throws IOException, ServletException {
		LoginUser loginUser = tokenService.getLoginUser( request );
		if ( StringUtils.isNotNull( loginUser ) ) {
			String userName = loginUser.getUsername();
			// 删除用户缓存记录
			tokenService.delLoginUser( loginUser.getToken() );
			// 记录用户退出日志
			AsyncManager.me().execute( AsyncFactory.recordLogininfor( userName, AdminConstants.LOGOUT, "退出成功" ) );
		}
		ServletUtil.renderString( response, JsonUtil.object2Json( AjaxResult.error( HttpStatus.SUCCESS, "退出成功" ) ) );
	}
}
