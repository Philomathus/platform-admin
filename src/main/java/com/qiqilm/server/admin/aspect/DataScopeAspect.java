package com.qiqilm.server.admin.aspect;

import com.qiqilm.server.admin.annotation.DataScope;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.SysRole;
import com.qiqilm.server.admin.domain.SysUser;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.SpringUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据过滤处理
 *
 * @author 77tv
 */
@Aspect
@Component
public class DataScopeAspect {
	/**
	 * 全部数据权限
	 */
	public static final String DATA_SCOPE_ALL = "1";

	/**
	 * 自定数据权限
	 */
	public static final String DATA_SCOPE_CUSTOM = "2";

	/**
	 * 仅本人数据权限
	 */
	public static final String DATA_SCOPE_SELF = "5";

	/**
	 * 数据权限过滤关键字
	 */
	public static final String DATA_SCOPE = "dataScope";

	/**
	 * 数据范围过滤
	 *
	 * @param joinPoint 切点
	 * @param user      用户
	 * @param userAlias 别名
	 */
	public static void dataScopeFilter( JoinPoint joinPoint, SysUser user, String userAlias ) {
		StringBuilder sqlString = new StringBuilder();

		for ( SysRole role : user.getRoles() ) {
			String dataScope = role.getDataScope();
			if ( DATA_SCOPE_ALL.equals( dataScope ) ) {
				sqlString = new StringBuilder();
				break;
			} else if ( DATA_SCOPE_SELF.equals( dataScope ) ) {
				if ( StringUtils.isNotBlank( userAlias ) ) {
					sqlString.append( StringUtils.format( " OR {}.user_id = {} ", userAlias, user.getUserId() ) );
				} else {
					// 数据权限为仅本人且没有userAlias别名不查询任何数据
					sqlString.append( " OR 1=0 " );
				}
			}
		}

		if ( StringUtils.isNotBlank( sqlString.toString() ) ) {
			Object params = joinPoint.getArgs()[ 0 ];
			if ( StringUtils.isNotNull( params ) && params instanceof BaseEntity ) {
				BaseEntity baseEntity = ( BaseEntity ) params;
				baseEntity.getParams().put( DATA_SCOPE, " AND (" + sqlString.substring( 4 ) + ")" );
			}
		}
	}

	// 配置织入点
	@Pointcut( "@annotation(com.qiqilm.server.admin.annotation.DataScope)" )
	public void dataScopePointCut() {
	}

	@Before( "dataScopePointCut()" )
	public void doBefore( JoinPoint point ) throws Throwable {
		handleDataScope( point );
	}

	protected void handleDataScope( final JoinPoint joinPoint ) {
		// 获得注解
		DataScope controllerDataScope = getAnnotationLog( joinPoint );
		if ( controllerDataScope == null ) {
			return;
		}
		// 获取当前的用户
		LoginUser loginUser = SpringUtils.getBean( TokenService.class ).getLoginUser( ServletUtil.getHttpServletRequest() );
		if ( StringUtils.isNotNull( loginUser ) ) {
			SysUser currentUser = loginUser.getUser();
			// 如果是超级管理员，则不过滤数据
			if ( StringUtils.isNotNull( currentUser ) && !currentUser.isAdmin() ) {
				dataScopeFilter( joinPoint, currentUser, controllerDataScope.userAlias() );
			}
		}
	}

	/**
	 * 是否存在注解，如果存在就获取
	 */
	private DataScope getAnnotationLog( JoinPoint joinPoint ) {
		Signature       signature       = joinPoint.getSignature();
		MethodSignature methodSignature = ( MethodSignature ) signature;
		Method          method          = methodSignature.getMethod();

		if ( method != null ) {
			return method.getAnnotation( DataScope.class );
		}
		return null;
	}
}
