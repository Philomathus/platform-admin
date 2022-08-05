package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.dto.PlatformUser;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WheelPoolCacheUtil {
    @Autowired
    private RedisUtil redisUtil;

    private static final long DAY_3_MILL = 259200000;

    public static final String WHEELPOOLLOTTERYLIST_KEY = Constants.LIVE_PREX + "wheelPoolLotteryList";

    public List<PlatformUser> getLotteryList() {
        long now = System.currentTimeMillis();
        return redisUtil.zReverseRangeByScore(WHEELPOOLLOTTERYLIST_KEY, now - DAY_3_MILL, now).stream()
                .map(v -> JsonUtil.json2Object(v, PlatformUser.class))
                .collect(Collectors.toList());
    }

    //每天凌晨0点执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void clean3daybefore() {
        if (!redisUtil.adminLock(EnumLock.adminTask, getClass().getSimpleName(), 60)) {
            return;
        }
        redisUtil.zRemoveRangeByScore(WHEELPOOLLOTTERYLIST_KEY, 0, System.currentTimeMillis() - DAY_3_MILL);
    }
}
