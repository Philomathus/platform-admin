package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.service.IPayAgentService;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Log4j2
@Component
public class PayAgentTask {
	@Autowired
	private RedisUtil        redisUtil;
	@Autowired
	private IPayAgentService payAgentService;

	@Scheduled( cron = "0 0/3 * * * ?" ) // 每3分钟执行一次
	public void confirmPayAgentOrder() {
		try {
			log.warn( "开始执行代付订单的超时查询 - 判断锁" );
			Boolean lock = redisUtil.strSetIfAbsent( "confirmPayAgentOrder", "1", Duration.ofSeconds( 30 ) );
			if ( lock != null && lock ) {
				log.warn( "开始执行代付订单的超时查询" );
				payAgentService.queryAgent4Status5Min();
			}
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
	}
}
