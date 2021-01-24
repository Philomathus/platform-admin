package com.qiqilm.server.admin.interceptor;

import com.qiqilm.server.admin.annotation.RepeatSubmit;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 防止重复提交拦截器
 *
 * @author 77tv
 */
@Component
public abstract class RepeatSubmitInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception {
		if ( handler instanceof HandlerMethod ) {
			HandlerMethod handlerMethod = ( HandlerMethod ) handler;
			Method        method        = handlerMethod.getMethod();
			RepeatSubmit  annotation    = method.getAnnotation( RepeatSubmit.class );
			if ( annotation != null ) {
				if ( this.isRepeatSubmit( request ) ) {
					AjaxResult ajaxResult = AjaxResult.error( "不允许重复提交，请稍后再试" );
					ServletUtil.renderString( response, JsonUtil.object2Json( ajaxResult ) );
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 验证是否重复提交由子类实现具体的防重复提交的规则
	 *
	 * @return
	 * @throws Exception
	 */
	public abstract boolean isRepeatSubmit( HttpServletRequest request );
}
