package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
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

    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;

    @Scheduled( fixedDelay = 300000, initialDelay = 30000 )
    public  void shuffle(){

        int queTime = sysConfigCacheUtil.getConfInt("host-shuffle-que",1);

        if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName(),600*queTime)){
            return;
        }

        try {
            liveVideoService.processVideoSort();
        } catch ( Exception e ) {
            log.error( "主播排序异常", e );
        }
    }
}
