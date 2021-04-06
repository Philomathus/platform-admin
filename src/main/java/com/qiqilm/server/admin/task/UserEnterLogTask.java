package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.service.ILiveLogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 用户进入直播间日志任务
 *
 * @author axing
 * @date 2021/01/18
 */
@Log4j2
@Component
public class UserEnterLogTask {

    @Resource
    private ILiveLogService liveLogService;



    @Scheduled( fixedDelay = 120000, initialDelay = 60000 )
    public void runTask() {

        try {
            liveLogService.banchUpdateEnterLog();
        }catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }

    }

}
