package com.qiqilm.server.admin.task.beat;

import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IGameDataLogService;
import com.qiqilm.server.admin.service.IGamePlatformService;
import com.qiqilm.server.admin.utils.LocalDateTimeUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏数据打码
 */
@Log4j2
@Component
public class GameDataTask {
    @Resource
    private IGameDataLogService  gameDataLogService;
    @Resource
    private IGamePlatformService gamePlatformService;
    @Resource
    private RedisUtil            redisUtil;

    private Map<Integer, String>     platformType = new HashMap<>();
    private Map<Integer, BigDecimal> beatRateMap  = new HashMap<>();

    @PostConstruct
    public void init() {
        //DynamicDataSourceContextHolder.setDataSourceKey("secondaryDataSource");
        for ( GamePlatform gm : gamePlatformService.selectGamePlatformList( new GamePlatform() ) ) {
            platformType.put( gm.getId(), gm.getGameTypeid() );
            beatRateMap.put( gm.getId(), gm.getRateBeat() );
        }
        //DynamicDataSourceContextHolder.clearDataSourceKey();
    }


    @Scheduled( fixedDelay = 30000, initialDelay = 1 )
    public void runTask() throws Exception {
        if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName() ) ) {
            return;
        }

        LocalDateTime endDay  = LocalDateTime.now();
        LocalDateTime starDay = endDay.minusMinutes( 3 );
        String        begin   = LocalDateTimeUtils.format( starDay );
        String        end     = LocalDateTimeUtils.format( endDay );
        if ( LocalDateTimeUtils.isSameDay( starDay, endDay ) ) {
            try {
                gameDataLogService.beatGameCodeAgent( begin, platformType, beatRateMap, begin, end, null, null );
            } catch ( Exception e ) {
                log.error( "1游戏拉取注单异常{}", e.getMessage(), e );
            }
            endDay  = LocalDateTime.now().minusMinutes( 5 );
            starDay = endDay.minusMinutes( 3 );
            begin   = LocalDateTimeUtils.format( starDay );
            end     = LocalDateTimeUtils.format( endDay );

            try {
                gameDataLogService.beatGameCodeAgent( begin, platformType, beatRateMap, begin, end, null, null );
            } catch ( Exception e ) {
                log.error( "4游戏拉取注单异常{}", e.getMessage(), e );
            }
        } else {
            end = LocalDateTimeUtils.format( starDay.plusMinutes( 5 ).toLocalDate().atStartOfDay() );
            try {
                gameDataLogService.beatGameCodeAgent( begin, platformType, beatRateMap, begin, end, null, null );
            } catch ( Exception e ) {
                log.error( "2游戏拉取注单异常{}", e.getMessage(), e );
            }
            begin = end;
            end   = LocalDateTimeUtils.format( endDay );
            try {
                gameDataLogService.beatGameCodeAgent( end, platformType, beatRateMap, begin, end, null, null );
            } catch ( Exception e ) {
                log.error( "3游戏拉取注单异常{}", e.getMessage(), e );
            }
        }

    }
}
