package com.qiqilm.server.admin.task.beat;

import com.qiqilm.server.admin.config.dds.DynamicDataSourceContextHolder;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.enums.EnumGamePlatform;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IGameDataLogService;
import com.qiqilm.server.admin.service.IGamePlatformService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;

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

    @Autowired
    private RedisUtil redisUtil;

    /*private String platformTypeId;
    private BigDecimal beatRate ;

    @PostConstruct
    public void init() {
        GamePlatform gamePlatform = gamePlatformService.selectGamePlatformById(EnumGamePlatform.CX_LIVE.getType());
        platformTypeId = gamePlatform.getGameTypeid();
        beatRate= gamePlatform.getRateBeat();
    }*/

    @Scheduled( fixedDelay = 50000, initialDelay=2000  )
    public void runPropTask() throws Exception {
        if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName())){
            return;
        }
        Date endDay  = new Date();
        Date starDay = DateFormatUtils.addMin( endDay, -2);

        try {
            DynamicDataSourceContextHolder.setDataSourceKey("secondaryDataSource");
            GamePlatform gamePlatform = gamePlatformService.selectGamePlatformById(EnumGamePlatform.CX_LIVE.getType());
            String platformTypeId = gamePlatform.getGameTypeid();
            BigDecimal beatRate = gamePlatform.getRateBeat();
            DynamicDataSourceContextHolder.clearDataSourceKey();
            gameDataLogService.beatLiveProp(platformTypeId,beatRate,starDay.getTime()/1000,endDay.getTime()/1000);
        }catch (Exception e){
            log.error("礼物拉取注单异常,",e);
        }

    }


}
