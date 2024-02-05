package com.qiqilm.server.admin.task;


import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.SmsFailLog;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.mapper.MessageSendMapper;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.RobotMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Log4j2
@Component
public class SmsFailMessageTask {
	@Autowired
	private SysConfigCacheUtil sysConfigCacheUtil;
	@Autowired
	private RedisUtil          redisUtil;
	@Autowired
	private MessageSendMapper  messageSendMapper;
	@Autowired
	private RobotMessage       robotMessage;

	//2分钟执行
	@Scheduled( fixedDelay = 120000, initialDelay = 1 )
	public void runTask() {
		if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 100 ) ) {
			return;
		}

		String flag = sysConfigCacheUtil.getConf( "messageBot" );
		String online_user_telegram = sysConfigCacheUtil.getConf( "online_user_telegram" );
		if ( "0".equals( flag )|| Strings.isBlank(online_user_telegram) ) {
			return;
		}

		String beginTime = null;
		String endTime   = null;
		try {
			beginTime = DateFormatUtils.formate( DateFormatUtils.addMin( new Date(), -3 ) );
			endTime = DateFormatUtils.formate( new Date() );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		List<SmsFailLog> result = messageSendMapper.smsFailMessage( beginTime, endTime );

		if ( result.size() > 0 ) {
			SmsFailLog smsFailLog = result.get( 0 );
			String text = "短信错误告警,请检查处理,异常次数:" + result.size()
					+ "\n运营商名称:" + smsFailLog.getSmsSubname()
					+ "\n异常原因:" + smsFailLog.getMessage();

			robotMessage.sendByChatId( text, online_user_telegram);
		}
	}
}
