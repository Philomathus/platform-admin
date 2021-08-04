package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.utils.HelpNoticeUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Log4j2
@Component
public class HelpNotice {

    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private HelpNoticeUtil helpNoticeUtil;

    public static final String NOTICE_KEY = Constants.LIVE_PREX + "77-notice";

    @Scheduled( fixedDelay = 240000, initialDelay = 60000 )
    public void notice(){
        if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName(),600)){
            return;
        }
        String help_notice = sysConfigCacheUtil.getConf("77_help_notice",null);
        String live_notice=sysConfigCacheUtil.getConf("77_live_notice",null);
        if (Strings.isBlank(help_notice) && Strings.isBlank(live_notice)){
            return;
        }
        Boolean exists = redisUtil.exists(NOTICE_KEY);
        if (exists == null || !exists) {
            if (!Strings.isBlank(help_notice)){
                helpNoticeUtil.sendMsg(help_notice);
                //添加缓存
                redisUtil.strSet(NOTICE_KEY,"77_help_notice");
                return;
            }
            if (!Strings.isBlank(live_notice)){
                //发送信息
                helpNoticeUtil.sendMsg(live_notice);
                //添加缓存
                redisUtil.strSet(NOTICE_KEY,"77_live_notice");
                return;
            }
        }

        String s = redisUtil.strGet(NOTICE_KEY);
        if (!Strings.isBlank(help_notice) &&  !Strings.isBlank(live_notice)){
            if (!Strings.isBlank(s)){
                if (s.equals("77_help_notice")){
                    //发送信息
                    helpNoticeUtil.sendMsg(live_notice);
                    //添加缓存
                    redisUtil.strSet(NOTICE_KEY,"77_live_notice");
                    return;
                }
                helpNoticeUtil.sendMsg(help_notice);
                //添加缓存
                redisUtil.strSet(NOTICE_KEY,"77_help_notice");
                return;
            }
        }

        if (Strings.isBlank(help_notice)){
            //发送信息
            helpNoticeUtil.sendMsg(live_notice);
            if (!s.equals("77_live_notice")){
                //添加缓存
                redisUtil.strSet(NOTICE_KEY,"77_live_notice");
            }
            return;
        }
        if (Strings.isBlank(live_notice)){
            helpNoticeUtil.sendMsg(help_notice);
            if (!s.equals("77_help_notice")){
                //添加缓存
                redisUtil.strSet(NOTICE_KEY,"77_help_notice");
            }
            return;
        }

    }

}
