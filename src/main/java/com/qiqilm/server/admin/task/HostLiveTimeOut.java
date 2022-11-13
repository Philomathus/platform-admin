package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.cache.VideoCacheUtil;
import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Component
@Log4j2
public class HostLiveTimeOut {
    @Autowired
    private RedisUtil          redisUtil;
    @Autowired
    private VideoCacheUtil     videoCacheUtil;
    @Autowired
    private ILiveVideoService  liveVideoService;
    @Resource
    private SysConfigCacheUtil sysConfigCacheUtil;

    @Scheduled( fixedDelay = 120000, initialDelay = 120000 )
    public void listenerMonitor() {
        if ( !LiveCenterConfig.me.isLiveCenter() ) {
            return;
        }
        if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 100 ) ) {
            return;
        }

        try {
            Set<String> liveVideos = videoCacheUtil.getAbortVideoByMonitorTime();
            liveVideos.forEach( videoId -> {
                if ( !redisUtil.exists( Constants.LIVE_PREX + "lock:host:" + videoId ) ) {
                    liveVideoService.close( Long.valueOf( videoId ), "timeOut" );
                    videoCacheUtil.delAbortVideoByMonitorTime( videoId );
                }

            } );

            liveVideoService.updateNowLine();
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }

    }

    @Scheduled( fixedDelay = 120000, initialDelay = 1000 )
    public void syncMainLiveSort() {
        if ( LiveCenterConfig.me.isLiveCenter() ) {
            return;
        }
        if ( !sysConfigCacheUtil.getConfBool( "sync_main_live_sort" ) ) {
            return;
        }
        try {
            liveVideoService.syncMainLiveSort();
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
    }
}
