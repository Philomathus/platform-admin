package com.qiqilm.server.admin.task.beat;

import com.qiqilm.server.admin.service.IGameDataLogService;
import com.qiqilm.server.admin.service.IGamePlatformService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * 送礼数据打码
 */
@Log4j2
@Component
public class LiveDataTask {


    @Autowired
    private IGameDataLogService gameDataLogService;
    @Autowired
    private IGamePlatformService gamePlatformService;



    @Scheduled( fixedDelay = 30000, initialDelay=5000  )
    public void runPropTask() throws Exception {

    }


    @Scheduled( fixedDelay = 30000, initialDelay=5000  )
    public void runOtherTask() throws Exception {

    }


}
