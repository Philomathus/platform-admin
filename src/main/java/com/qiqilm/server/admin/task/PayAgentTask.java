package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IPayAgentService;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Log4j2
@Component
public class PayAgentTask {
    @Resource
    private RedisUtil        redisUtil;
    @Resource
    private IPayAgentService payAgentService;

    @Scheduled( cron = "0 0/3 * * * ?" ) // 每3分钟执行一次
    public void confirmPayAgentOrder() {
        try {
            log.warn( "开始执行代付订单的超时查询 - 判断锁" );
            if ( redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 30 ) ) {
                log.warn( "开始执行代付订单的超时查询" );
                payAgentService.queryAgent4Status5Min();
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
    }
}
