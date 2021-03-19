package com.qiqilm.server.admin.task.beat;

import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.service.IGameDataLogService;
import com.qiqilm.server.admin.service.IGamePlatformService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
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

    @Value( "${spring.profiles.active}" )
    private String profile;
    private Map<Integer,String> platformType = new HashMap<>();
    private Map<Integer, BigDecimal> beatRateMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for(GamePlatform gm: gamePlatformService.selectGamePlatformList(new GamePlatform())){
            platformType.put(gm.getId(),gm.getGameTypeid());
            beatRateMap.put(gm.getId(),gm.getRateBeat());
        }
    }


    @Scheduled( fixedDelay = 30000, initialDelay=1 )
    public void runTask() throws Exception {

        Date endDay  = new Date();

        Date starDay = DateFormatUtils.addMin( endDay, -10);

        gameDataLogService.beatCode(platformType,profile, DateFormatUtils.formate( starDay ),DateFormatUtils.formate( endDay ),null,null);
    }
}
