package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.service.IMemberRechargeLogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RechargeLogTask {
	@Autowired
	private IMemberRechargeLogService memberRechargeLogService;

	@Scheduled( cron = "0 * * * * ?" )
	public void runTask() {
		log.info( "执行30分钟未人工入款处理充值记录自动失败条数："+memberRechargeLogService.checkRechargeLogFail() );
	}
}
