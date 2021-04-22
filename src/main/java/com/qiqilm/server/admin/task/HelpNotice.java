package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.utils.HelpNoticeUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Log4j2
@Component
public class HelpNotice {

    @Autowired
    private ImApi imApi;

    @Autowired
    private ILiveVideoService liveVideoService;

    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private HelpNoticeUtil helpNoticeUtil;

    @Scheduled( fixedDelay = 900000, initialDelay = 60000 )
    public void notice(){
        if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName(),600)){
            return;
        }

        String text = sysConfigCacheUtil.getConf("77_help_notice",null);
        helpNoticeUtil.sendMsg(text);


    }

}
