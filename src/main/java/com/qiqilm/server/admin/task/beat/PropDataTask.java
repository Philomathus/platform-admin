package com.qiqilm.server.admin.task.beat;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * 送礼数据打码
 */
@Log4j2
@Component
public class PropDataTask {

    @Value( "${spring.profiles.active}" )
    private String profile;


    @Scheduled( fixedDelay = 30000, initialDelay=10000 )
    public void runTask() throws Exception {

    }
}
