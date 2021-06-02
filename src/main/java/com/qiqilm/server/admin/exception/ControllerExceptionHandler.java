package com.qiqilm.server.admin.exception;

import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.UserDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title: ControllerExceptionHandler</p>
 * <p>Description: 控制器异常处理</p>
 *
 * @author admin
 */
@ControllerAdvice
@Slf4j
@Component
public abstract class ControllerExceptionHandler {
	/**
	 * 控制器异常处理入口
	 *
	 * @param e 异常信息
	 */
	@ExceptionHandler( Throwable.class )
	@ResponseBody
	@ResponseStatus( HttpStatus.OK )
	public RspBase resolveException( Exception e ) {
		HttpServletRequest request = ServletUtil.getHttpServletRequest();
		if ( e instanceof BusinessException ) {
			return RspBase.businessError( e.getMessage() );
		} else if ( e instanceof SessionExpireException ) {
			RspBase rspError = new RspBase();
			rspError.setCode( ErrCode.SESSION_EXPIRE_FAIL );
			rspError.setMsg( e.getMessage() );
			return rspError;
		} else if ( e instanceof AccessDeniedException ) {
			return RspBase.businessError(e.getMessage());
		} else if (e instanceof BindException){
			BindException exec = (BindException) e;
			if(exec.hasErrors()){
				RspBase rspError = new RspBase();
				rspError.setCode(1);
				String message = exec.getBindingResult().getAllErrors().get(0).getDefaultMessage();
				rspError.setMsg("数据校验失败，请检查!");
				log.error( "异常请求url:{},IP:{},msg:{}", request.getRequestURL().toString(), UserDataUtil.getIp( request ),message);
				return rspError;
			}
		}
		log.error( "异常请求url:{},IP:{},msg:{}", request.getRequestURL().toString(), UserDataUtil.getIp( request ),
				e.getMessage(), e );
		return RspBase.businessError( "服务器异常,请联系客服" );
	}
}
