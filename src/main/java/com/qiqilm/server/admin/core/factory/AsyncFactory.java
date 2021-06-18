package com.qiqilm.server.admin.core.factory;

import com.qiqilm.server.admin.constant.AdminConstants;
import com.qiqilm.server.admin.domain.SysLogininfor;
import com.qiqilm.server.admin.domain.SysOperLog;
import com.qiqilm.server.admin.service.ISysLogininforService;
import com.qiqilm.server.admin.service.ISysOperLogService;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.SpringUtils;
import com.qiqilm.server.admin.utils.UserDataUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 *
 * @author 77tv
 */
public class AsyncFactory {
	private static final Logger sys_user_logger = LoggerFactory.getLogger( "sys-user" );

	/**
	 * 记录登录信息
	 *
	 * @param username 用户名
	 * @param status   状态
	 * @param message  消息
	 * @param args     列表
	 * @return 任务task
	 */
	public static TimerTask recordLogininfor( final String username, final String status, final String message,
											  final Object... args ) {
		HttpServletRequest request   = ServletUtil.getHttpServletRequest();
		final UserAgent    userAgent = UserAgent.parseUserAgentString( request.getHeader( "User-Agent" ) );
		final String       ip        = UserDataUtil.getIp( request );
		return new TimerTask() {
			@Override
			public void run() {
				String address = "";
				// 打印信息到日志
				String s = AsyncFactory.getBlock( ip ) +
						address +
						AsyncFactory.getBlock( username ) +
						AsyncFactory.getBlock( status ) +
						AsyncFactory.getBlock( message );
				sys_user_logger.info( s, args );
				// 获取客户端操作系统
				String os = userAgent.getOperatingSystem().getName();
				// 获取客户端浏览器
				String browser = userAgent.getBrowser().getName();
				// 封装对象
				SysLogininfor logininfor = new SysLogininfor();
				logininfor.setUserName( username );
				logininfor.setIpaddr( ip );
				//logininfor.setLoginLocation( address );
				logininfor.setBrowser( browser );
				logininfor.setOs( os );
				logininfor.setMsg( message );
				// 日志状态
				if ( AdminConstants.LOGIN_SUCCESS.equals( status ) || AdminConstants.LOGOUT.equals( status ) ) {
					logininfor.setStatus( AdminConstants.SUCCESS );
				} else if ( AdminConstants.LOGIN_FAIL.equals( status ) ) {
					logininfor.setStatus( AdminConstants.FAIL );
				}
				// 插入数据
				SpringUtils.getBean( ISysLogininforService.class ).insertLogininfor( logininfor );
			}
		};
	}

	/**
	 * 操作日志记录
	 *
	 * @param operLog 操作日志信息
	 * @return 任务task
	 */
	public static TimerTask recordOper( final SysOperLog operLog ) {
		return new TimerTask() {
			@Override
			public void run() {
				// 远程查询操作地点 AddressUtils.getRealAddressByIP( operLog.getOperIp() )
				operLog.setOperLocation( "" );
				SpringUtils.getBean( ISysOperLogService.class ).insertOperlog( operLog );
			}
		};
	}

	private static String getBlock( Object msg ) {
		if ( msg == null ) {
			msg = "";
		}
		return "[" + msg.toString() + "]";
	}
}
