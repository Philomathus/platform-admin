package com.qiqilm.server.admin.task.beat;

import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IGameDataLogService;
import com.qiqilm.server.admin.service.IGamePlatformService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏数据打码
 */
@Log4j2
@Component
public class GameDataTask {
    @Autowired
    private IGameDataLogService gameDataLogService;
    @Autowired
    private IGamePlatformService gamePlatformService;
    @Autowired
    private RedisUtil redisUtil;

    @Value( "${spring.profiles.active}" )
    private String profile;
    private Map<Integer,String> platformType = new HashMap<>();
    private Map<Integer, BigDecimal> beatRateMap = new HashMap<>();

    @PostConstruct
    public void init() {
        //DynamicDataSourceContextHolder.setDataSourceKey("secondaryDataSource");
        for(GamePlatform gm: gamePlatformService.selectGamePlatformList(new GamePlatform())){
            platformType.put(gm.getId(),gm.getGameTypeid());
            beatRateMap.put(gm.getId(),gm.getRateBeat());
        }
        //DynamicDataSourceContextHolder.clearDataSourceKey();
    }


    @Scheduled( fixedDelay = 30000, initialDelay=1 )
    public void runTask() throws Exception {
        if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName())){
            return;
        }

        Date endDay  = new Date();
        Date starDay = DateFormatUtils.addMin( endDay, -2);
        String begin = DateFormatUtils.formate( starDay );
        if(! DateUtils.isSameDay(starDay,endDay)){
            Date bDay = DateFormatUtils.getTomorrowMorning(starDay);
            String end = DateFormatUtils.formate( bDay );

            try {
                gameDataLogService.beatGameCodeAgent(begin,platformType,beatRateMap,profile, begin,end,null,null);

            }catch (Exception e){
                log.error("1游戏拉取注单异常,",e);
            }

            begin = end;
            end = DateFormatUtils.formate( endDay );

            try {
                gameDataLogService.beatGameCodeAgent(end,platformType,beatRateMap,profile, begin,end,null,null);

            }catch (Exception e){
                log.error("2游戏拉取注单异常,",e);
            }
            return;
        }


        try {
            gameDataLogService.beatGameCodeAgent(begin,platformType,beatRateMap,profile, DateFormatUtils.formate( starDay ),DateFormatUtils.formate( endDay ),null,null);

        }catch (Exception e){
            log.error("新游戏拉取注单异常,",e);
        }


    }
}
