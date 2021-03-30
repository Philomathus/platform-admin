package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.utils.Constants;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.VideoCacheUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Log4j2
public class HostLiveTimeOut {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private VideoCacheUtil videoCacheUtil;
    @Autowired
    private ILiveVideoService liveVideoService;

    @Scheduled( fixedDelay = 60000, initialDelay = 60000 )
    public void listenerMonitor() {
        if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName(),60)){
            return;
        }

        try {
            Set<String> liveVideos = videoCacheUtil.getAbortVideoByMonitorTime();
            liveVideos.forEach( videoId -> {
                if(!redisUtil.exists(Constants.redisPRex + "addVideo:" + videoId)){
                    liveVideoService.close( Long.valueOf(videoId), "timeOut" );
                    redisUtil.unlink(Constants.redisPRex + "addVideo:" + videoId);
                }

            } );

            liveVideoService.updateNowLine();
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }

    }

}
