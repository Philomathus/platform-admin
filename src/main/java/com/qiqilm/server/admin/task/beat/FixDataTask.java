package com.qiqilm.server.admin.task.beat;

import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.MemberGameDatafix;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.mapper.MemberGameDatafixMapper;
import com.qiqilm.server.admin.service.IGameDataLogService;
import com.qiqilm.server.admin.service.IGamePlatformService;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 游戏数据打码
 */
@Log4j2
@Component
public class FixDataTask {
    @Autowired
    private IGameDataLogService gameDataLogService;
    @Autowired
    private IGamePlatformService gamePlatformService;
    @Autowired
    private MemberGameDatafixMapper memberGameDatafixMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${spring.profiles.active}")
    private String profile;
    private Map<Integer, String> platformType = new HashMap<>();
    private Map<Integer, BigDecimal> beatRateMap = new HashMap<>();

    @PostConstruct
    public void init() {
        //DynamicDataSourceContextHolder.setDataSourceKey("secondaryDataSource");
        for (GamePlatform gm : gamePlatformService.selectGamePlatformList(new GamePlatform())) {
            platformType.put(gm.getId(), gm.getGameTypeid());
            beatRateMap.put(gm.getId(), gm.getRateBeat());
        }
        //DynamicDataSourceContextHolder.clearDataSourceKey();
    }


    @Scheduled(fixedDelay = 600000, initialDelay = 1)
    public void runTask() throws Exception {
        if (!redisUtil.adminLock(EnumLock.adminTask, getClass().getSimpleName())) {
            return;
        }
        MemberGameDatafix query = new MemberGameDatafix();
        query.setStatus( 0 );
        List<MemberGameDatafix> memberGameDatafixes = memberGameDatafixMapper.selectMemberGameDatafixList( query );
        for ( MemberGameDatafix memberGameDatafix : memberGameDatafixes ) {
            if (memberGameDatafix == null) {
                continue;
            }
            Long platformId = memberGameDatafix.getPlatformId();
            if (platformId == 3 || platformId == 4) {
                continue;
            }
            String platformid = platformId.toString();
            try {
                gameDataLogService.beatGameCodeAgent(memberGameDatafix.getGameStartTime(), platformType, beatRateMap, profile, memberGameDatafix.getGameStartTime(), memberGameDatafix.getGameEndTime(), memberGameDatafix.getUserId(), platformid);
                MemberGameDatafix data = new MemberGameDatafix();
                data.setId(memberGameDatafix.getId());
                data.setStatus(1);
                memberGameDatafixMapper.updateMemberGameDatafix(data);
            } catch (Exception e) {
                log.error("修复游戏注定数据失败,", e);
            }
        }
    }
}
