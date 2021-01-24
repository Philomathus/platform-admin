package com.qiqilm.server.admin.aspect;

import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志方面
 *
 * @author dan
 * @version 1.0
 * @date 2020/11/04
 */
@Log4j2
@Component
@Aspect
public class RequestLogAspect {

	public static final String getTime() {
		return new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( new Date() );
	}

	@Pointcut( "execution(* com.qiqilm.server.admin.controller..*(..))" )
	public void requestServer() {
	}

	@Around( "requestServer()" )
	public Object doAround( ProceedingJoinPoint proceedingJoinPoint ) throws Throwable {
		long               start       = System.currentTimeMillis();
		HttpServletRequest request     = ServletUtil.getHttpServletRequest();
		Object             result      = proceedingJoinPoint.proceed();
		RequestInfo        requestInfo = new RequestInfo();
		requestInfo.setIp( request.getRemoteAddr() );
		requestInfo.setUrl( request.getRequestURL().toString() );
		requestInfo.setHttpMethod( request.getMethod() );
		requestInfo.setClassMethod( String.format( "%s.%s", proceedingJoinPoint.getSignature().getDeclaringTypeName(),
				proceedingJoinPoint.getSignature().getName() ) );
		requestInfo.setRequestParams( getRequestParamsByProceedingJoinPoint( proceedingJoinPoint ) );
		requestInfo.setResult( result );
		requestInfo.setTimeCost( System.currentTimeMillis() - start );
		printObject( start, proceedingJoinPoint.getSignature().getDeclaringTypeName(),
				proceedingJoinPoint.getSignature().getName(), "成功", null, String.valueOf( result ) );
		return result;
	}

	@AfterThrowing( pointcut = "requestServer()", throwing = "e" )
	public void doAfterThrow( JoinPoint joinPoint, RuntimeException e ) {
		HttpServletRequest request          = ServletUtil.getHttpServletRequest();
		RequestErrorInfo   requestErrorInfo = new RequestErrorInfo();
		requestErrorInfo.setIp( request.getRemoteAddr() );
		requestErrorInfo.setUrl( request.getRequestURL().toString() );
		requestErrorInfo.setHttpMethod( request.getMethod() );
		requestErrorInfo.setClassMethod( String.format( "%s.%s", joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName() ) );
		requestErrorInfo.setRequestParams( getRequestParamsByJoinPoint( joinPoint ) );
		requestErrorInfo.setException( e );
		printObject( System.currentTimeMillis(), joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(), "失败", e.getMessage(), String.valueOf( requestErrorInfo ) );
	}

	public boolean printObject( long start, String className, String methodName, String resultType, String exception,
								String jsonString ) {
		HttpServletRequest request  = ServletUtil.getHttpServletRequest();
		String             parmeter = JsonUtil.object2Json( request.getParameterMap() );
		StringBuilder      sb       = new StringBuilder( 1000 );
		sb.append( "\n" ).append( "----------------------------" ).append( "请求时间    " ).append( getTime() ).append(
				"----------------------------\n" );
		//类名称
		sb.append( "Class: " ).append( className ).append( "\n" );
		//方法名称
		sb.append( "Method    : " ).append( methodName ).append( "\n" );
		//请求方式  post\put\get 等等
		sb.append( "RequestMethod    : " ).append( request.getMethod() ).append( "\n" );
		//所有的请求参数
		sb.append( "Params    : " ).append( parmeter ).append( "\n" );
		//成功或失败
		sb.append( "Result       : " ).append( resultType ).append( "\n" );
		if ( exception != null ) {
			sb.append( "Exception       : " ).append( exception ).append( "\n" );
		}
		//部分请求链接
		sb.append( "URI       : " ).append( request.getRequestURI() ).append( "\n" );
		//完整的请求链接
		sb.append( "Url    : " ).append( request.getRequestURL() ).append( "\n" );
		//请求方的 ip地址
		sb.append( "request IP: " ).append( request.getRemoteHost() ).append( "\n" );
		//获取sessionId
		sb.append( "session ID: " ).append( request.getSession().getId() ).append( "\n" );
		//接口时间
		sb.append( "request Time: " ).append( ( System.currentTimeMillis() - start ) + "ms" ).append( "\n" );
		//接口时间
		sb.append( "Response Info: " ).append( jsonString ).append( "\n" );
		//控制台打印并保存到文件
		log.info( sb.toString() );
		return true;
	}

	/**
	 * 获取入参
	 *
	 * @return
	 */
	private Map<String, Object> getRequestParamsByProceedingJoinPoint( ProceedingJoinPoint proceedingJoinPoint ) {
		//参数名
		String[] paramNames = ( ( MethodSignature ) proceedingJoinPoint.getSignature() ).getParameterNames();
		//参数值
		Object[] paramValues = proceedingJoinPoint.getArgs();

		return buildRequestParam( paramNames, paramValues );
	}

	private Map<String, Object> getRequestParamsByJoinPoint( JoinPoint joinPoint ) {
		//参数名
		String[] paramNames = ( ( MethodSignature ) joinPoint.getSignature() ).getParameterNames();
		//参数值
		Object[] paramValues = joinPoint.getArgs();

		return buildRequestParam( paramNames, paramValues );
	}

	private Map<String, Object> buildRequestParam( String[] paramNames, Object[] paramValues ) {
		Map<String, Object> requestParams = new HashMap<>();
		for ( int i = 0; i < paramNames.length; i++ ) {
			Object value = paramValues[ i ];
			//如果是文件对象
			if ( value instanceof MultipartFile ) {
				MultipartFile file = ( MultipartFile ) value;
				value = file.getOriginalFilename();  //获取文件名
			}
			requestParams.put( paramNames[ i ], value );
		}

		return requestParams;
	}

	@Data
	public class RequestInfo {
		private String ip;
		private String url;
		private String httpMethod;
		private String classMethod;
		private Object requestParams;
		private Object result;
		private Long   timeCost;
	}

	@Data
	public class RequestErrorInfo {
		private String           ip;
		private String           url;
		private String           httpMethod;
		private String           classMethod;
		private Object           requestParams;
		private RuntimeException exception;
	}
}
