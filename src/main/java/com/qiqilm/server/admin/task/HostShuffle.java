package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class HostShuffle {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ILiveVideoService liveVideoService;

    @Scheduled( fixedDelay = 600000, initialDelay = 60000 )
    public  void shuffle(){

        if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName(),500)){
            return;
        }


        try {
            liveVideoService.processVideoSort();
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
    }
}
