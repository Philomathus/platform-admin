package com.qiqilm.server.admin.task;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class HostLiveGiftFix {


    @Scheduled( fixedDelay = 60000, initialDelay = 60000 )
    public void listenerMonitor() {

    }
}
