package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IMemberOnlineService;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Log4j2
@Component
public class MemOnlineCutTask {
    @Resource
    private IMemberOnlineService IMemberOnlineService;

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        IMemberOnlineService.cutTableOnline(3);
    }
    //每天凌晨4点执行
    @Scheduled(cron = "0 0 8 * * ?")
    public void cutGameDataLog(){
        if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 900 ) ) {
            return;
        }
        IMemberOnlineService.cutTableOnline(3);
    }
}
