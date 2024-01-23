package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.ILiveLogService;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.RobotMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 用户进入直播间日志任务
 *
 * @author axing
 * @date 2021/01/18
 */
@Log4j2
@Component
public class UserEnterLogTask {
    @Resource
    private RobotMessage       robotMessage;
    @Resource
    private RedisUtil          redisUtil;
    @Resource
    private SysConfigCacheUtil sysConfigCacheUtil;
    @Resource
    private ILiveLogService    liveLogService;

    @Value( "${spring.profiles.active}" )
    private String profile;

    @Scheduled( fixedDelay = 1800000, initialDelay = 600000 )
    public void runTask() {
        if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 1000 ) ) {
            return;
        }
        if ( !profile.startsWith( "77" ) || profile.equals( "7700" ) ) {
            return;
        }

        long size  = -1;
        long count = -1;
        try {
            Map<String, Long> map = liveLogService.banchUpdateEnterLog();
            if ( !CollectionUtils.isEmpty( map ) ) {
                size  = map.get( "size" );
                count = map.get( "cmapSize" );
            }
        } catch ( Exception e ) {
            log.error( "进直播间会员数异常", e );
        }

        String online_user_telegram = sysConfigCacheUtil.getConf( "online_user_telegram" );
        if ( Strings.isBlank( online_user_telegram ) ) {
            return;
        }

        try {
            String paytext = "30分钟进直播间人次:" + size + "进直播间人数:" + count;
            if ( size < 0 ) {
                paytext = "30分钟进直播间人数为0";
            }
            robotMessage.sendByChatId( paytext, online_user_telegram );

        } catch ( Exception e ) {
            log.error( "电报发送消息失败", e );
        }
    }
}
