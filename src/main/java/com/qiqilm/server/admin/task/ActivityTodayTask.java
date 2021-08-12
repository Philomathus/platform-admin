package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.mapper.MemberQuestMapper;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;


/**
 * 每日任务重置调度
 */
@Log4j2
@Component
public class ActivityTodayTask {

    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private MemberQuestMapper memberQuestMapper;

    //每天凌晨0点执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanDayTaskStatus(){
        if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 900 ) ) {
            return;
        }
        //任务有效时间 预留
//        String taskRangeTime= DateFormatUtils.getZeroToDayOver();
        //重置每日任务状态、打码量和有效时间
        memberQuestMapper.resetDayTaskStatus();
    }
}
