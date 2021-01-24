package com.qiqilm.server.admin.security.handle;

import com.qiqilm.server.admin.constant.HttpStatus;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 *
 * @author 77tv
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
	private static final long serialVersionUID = -8970718410437077606L;

	@Override
	public void commence( HttpServletRequest request, HttpServletResponse response, AuthenticationException e )
			throws IOException {
		int    code = HttpStatus.UNAUTHORIZED;
		String msg  = StringUtils.format( "请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI() );
		ServletUtil.renderString( response, JsonUtil.object2Json( AjaxResult.error( code, msg ) ) );
	}
}
