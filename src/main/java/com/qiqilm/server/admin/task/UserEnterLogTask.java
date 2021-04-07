package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.ILiveLogService;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.RobotMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 用户进入直播间日志任务
 *
 * @author axing
 * @date 2021/01/18
 */
@Log4j2
@Component
public class UserEnterLogTask {
    @Autowired
    private RobotMessage robotMessage;

    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;

    @Resource
    private ILiveLogService liveLogService;


    @Value( "${spring.profiles.active}" )
    private String profile;

    @Autowired
    private RedisUtil redisUtil;

    @Scheduled( fixedDelay = 600000, initialDelay = 60000 )
    public void runTask() {

        if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 300 ) ) {
            return;
        }

        String flag = sysConfigCacheUtil.getConf( "messageBot" );
        String online_user_telegram = sysConfigCacheUtil.getConf( "online_user_telegram" );
        if ( "0".equals( flag ) ) {
            return ;
        }
        if(!profile.startsWith("77")||profile.equals("7700")){
            return ;
        }
        int count ;

        try {
            count = liveLogService.banchUpdateEnterLog();
        }catch (Exception e){
            log.info("批量插入进直播间会员数异常",e);
            count = 0;
        }

        try {

            try {
                String paytext="10分钟直播间活跃人数:"+count;
                robotMessage.sendByChatId(paytext, online_user_telegram);

            } catch (Exception e) {
                log.error("电报发送消息失败" + e.getMessage());
            }
        }catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }

    }

}
