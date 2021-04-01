package com.qiqilm.server.admin.task;


import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IMemberWithdrawLogService;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.RobotMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class MessageSendTask {

	@Autowired
	private IMemberWithdrawLogService memberWithdrawLogService;

	@Autowired
	private RedisUtil          redisUtil;
	@Autowired
	private SysConfigCacheUtil sysConfigCacheUtil;
	@Autowired
	private RobotMessage       robotMessage;

	@Scheduled( fixedDelay = 300000, initialDelay = 1 )
	public void runTask() {
//		if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 250 ) ) {
//			return;
//		}

		String flag = sysConfigCacheUtil.getConf( "messageBot" );
		if ( flag.equals( "0" ) ) {
			return;
		}
		List<MemberWithdrawLog> list = memberWithdrawLogService.getWithdrawLogList();
		if ( list.size() > 0 ) {
			StringBuffer bf = new StringBuffer( "超过10分钟未处理的出款总数:" );
			bf.append( list.size() + "\n" );
			int i = 1;
			for ( MemberWithdrawLog memberWithdrawLog : list ) {
				bf.append( i + " 用户ID:" + memberWithdrawLog.getMemberId()
						+ " 金额:" + memberWithdrawLog.getWithdrawMoney() + "\n" );
				i++;
			}
			robotMessage.send( bf.toString() );
		}
	}
}
