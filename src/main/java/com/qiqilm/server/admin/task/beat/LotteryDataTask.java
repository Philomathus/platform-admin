package com.qiqilm.server.admin.task.beat;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.enums.EnumGamePlatform;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IGameDataLogService;
import com.qiqilm.server.admin.service.IGamePlatformService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 彩票数据打码
 */
@Log4j2
@Component
public class LotteryDataTask {


    @Autowired
    private IGameDataLogService gameDataLogService;
    @Autowired
    private IGamePlatformService gamePlatformService;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;
    @Value( "${spring.profiles.active}" )
    private String profile;

    private String platformTypeId;
    private BigDecimal beatRate ;


    @PostConstruct
    public void init() {
        GamePlatform gamePlatform = gamePlatformService.selectGamePlatformById(EnumGamePlatform.CX_LOTTERY.getType());
        platformTypeId = gamePlatform.getGameTypeid();
        beatRate= gamePlatform.getRateBeat();

    }
    @Scheduled( fixedDelay = 60000, initialDelay=5000 )
    public void runTask() throws Exception {
        if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName())){
            return;
        }

        if(!profile.startsWith("77")){
            return;
        }

        String lottery_telegram = sysConfigCacheUtil.getConf( "lottery_telegram" );

        Date endDay  = new Date();
        Date starDay = DateFormatUtils.addMin( endDay, -3);
        String start = DateFormatUtils.formate( starDay );
        String end = DateFormatUtils.formate( endDay );
        try {
            gameDataLogService.beatLotteryCode(lottery_telegram,platformTypeId,beatRate,start,end);
        }catch (Exception e){
            log.error("彩票拉取注单异常,",e);
        }

    }
}
