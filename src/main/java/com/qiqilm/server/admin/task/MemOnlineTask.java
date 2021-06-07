package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.service.IMemberOnlineService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Log4j2
@Component
public class MemOnlineTask {
    @Resource
    private IMemberOnlineService IMemberOnlineService;

    @PostConstruct
    public void init() {
        IMemberOnlineService.cutTableOnline(3);
    }
    //每天凌晨4点执行
    @Scheduled(cron = "0 0 4 * * ?")
    public void cutGameDataLog(){
        IMemberOnlineService.cutTableOnline(3);
    }
}
