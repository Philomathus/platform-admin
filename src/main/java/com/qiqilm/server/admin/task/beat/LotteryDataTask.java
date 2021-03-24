package com.qiqilm.server.admin.task.beat;

import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.enums.EnumGamePlatform;
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

    private String platformTypeId;
    private BigDecimal beatRate ;


    @PostConstruct
    public void init() {
        GamePlatform gamePlatform = gamePlatformService.selectGamePlatformById(EnumGamePlatform.CX_LOTTERY.getType());
        platformTypeId = gamePlatform.getGameTypeid();
        beatRate= gamePlatform.getRateBeat();

    }
    @Scheduled( fixedDelay = 30000, initialDelay=5000 )
    public void runTask() throws Exception {
        Date endDay  = new Date();
        Date starDay = DateFormatUtils.addMin( endDay, -10);
        String start = DateFormatUtils.formate( starDay );
        String end = DateFormatUtils.formate( endDay );
        for(int i=0;i<10;i++){
            try {
                gameDataLogService.beatLotteryCode(platformTypeId,beatRate,String.valueOf(i),start,end);
            }catch (Exception e){
                log.error("彩票拉取注单异常,",e);
            }
        }

    }
}
