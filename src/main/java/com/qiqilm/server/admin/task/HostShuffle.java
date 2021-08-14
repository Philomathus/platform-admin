package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Log4j2
@Component
public class HostShuffle {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ILiveVideoService liveVideoService;
    @Value( "${spring.profiles.active}" )
    private String profile;
    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;

    @Scheduled( fixedDelay = 300000, initialDelay = 30000 )
    public  void shuffle(){

        int queTime = sysConfigCacheUtil.getConfInt("host-shuffle-que",1);

        if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName(),600*queTime)){
            return;
        }

        if(!redisUtil.lock( "host:shuffle" + profile, 120 )){
            log.error( "2分钟内有主播上下播本次推荐位置乱序忽略");
            return;
        }

        try {
            liveVideoService.processVideoSort();
        } catch ( Exception e ) {
            log.error( "主播排序异常", e );
        }
    }
}
