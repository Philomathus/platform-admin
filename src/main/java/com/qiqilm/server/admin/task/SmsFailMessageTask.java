package com.qiqilm.server.admin.task;


import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.SmsFailLog;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.mapper.MessageSendMapper;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.RobotMessage;
import lombok.extern.log4j.Log4j2;
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
		if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 119 ) ) {
			return;
		}

		String flag = sysConfigCacheUtil.getConf( "messageBot" );
		if ( flag.equals( "0" ) ) {
			return;
		}

		String beginTime = null;
		String endTime   = null;
		try {
			beginTime = DateFormatUtils.formate( DateFormatUtils.addMin( new Date(), -3 ) );
			endTime = DateFormatUtils.formate( new Date() );
		} catch ( Exception e ) {
			e.getMessage();
		}
		List<SmsFailLog> result = messageSendMapper.smsFailMessage( beginTime, endTime );

		if ( result.size() > 0 ) {
			String text = "短信错误告警,请检查处理,异常次数:" + result.size() + "\n异常原因:" + result.get( 0 );

			robotMessage.sendByChatId( text, "-485027924" );
		}
	}
}
