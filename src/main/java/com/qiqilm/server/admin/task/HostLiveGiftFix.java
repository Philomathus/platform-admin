package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class HostLiveGiftFix {
    @Autowired
    private ILiveVideoService liveVideoService;
    @Autowired
    private RedisUtil redisUtil;

    @Value( "${spring.profiles.active}" )
    private String profile;

    @Scheduled(cron="0 0 2 * * ?" )
    public void listenerMonitor() {

        if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName(),900)){
            return;
        }

        if(profile.equals("7706")||profile.equals("7705")||profile.equals("7710")||profile.equals("7711")||profile.equals("77mm")){
            return;
        }

        liveVideoService.countHostGift(profile);

    }
}
