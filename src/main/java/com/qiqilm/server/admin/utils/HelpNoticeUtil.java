package com.qiqilm.server.admin.utils;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.im.MessageType;
import com.qiqilm.server.admin.service.ILiveVideoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知工具类
 *
 * @author axing
 * @date 2021/04/20
 */
@Log4j2
@Component
public class HelpNoticeUtil implements Serializable {
    @Autowired
    private ImApi imApi;

    @Autowired
    private ILiveVideoService liveVideoService;

    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;

    /**
     * 发送消息
     *
     * @param text 文本
     */
    public void sendMsg(String text) {
        if(text==null){
            return;
        }

        HashMap<String, Object> ext = new HashMap<>();
        ext.put( "type", 0); //普通消息
        ext.put( "fonts_color", "" );
        ext.put( "text", text);
        Map<String,Object> info = new HashMap<>();
        info.put("user_id","admin");
        info.put("user_level","50");
        info.put("nick_name",sysConfigCacheUtil.getConf( "77_help_nick_name" ));
        info.put("officer","2");
        info.put("guardType","2");
        ext.put( "sender", info );

        MessageType messageType = MessageType.TIMCustomElem.setData( JsonUtil.object2Json( ext ) );
        for(String groupId:liveVideoService.selectOnlineLiveGroups()){
            try {
                imApi.sendGroupMessage( groupId, "admin", messageType );
            }catch (Exception e){
                log.error("小助手发消息失败",e);
            }

        }
    }
}
