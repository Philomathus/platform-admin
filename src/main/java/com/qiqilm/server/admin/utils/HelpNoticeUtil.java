package com.qiqilm.server.admin.utils;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.imserver.ImServerUtils;
import com.qiqilm.server.admin.mapper.LiveVideoMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    private LiveVideoMapper    liveVideoMapper;
    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;

    @Value( "${live.encrypt.privateKey}" )
    private String liveRsaPrivateKey;

    @Value( "${spring.profiles.active}" )
    private String profile;

    @Autowired
    private ImApi         imApi;
    @Resource
    private ImServerUtils imServerUtils;

    /**
     * 所有直播间发送消息
     *
     * @param text 文本
     */
    public void sendMsg( String text ) {
        if ( text == null ) {
            return;
        }
        String                  agent = profile;
        HashMap<String, Object> ext   = new HashMap<>();
        ext.put( "type", 0 ); //普通消息
        ext.put( "fonts_color", "" );
        ext.put( "text", text );
        Map<String, Object> info = new HashMap<>();
        info.put( "user_id", "admin" );
        info.put( "user_level", "50" );
        info.put( "nick_name", sysConfigCacheUtil.getConf( "77_help_nick_name" ) );
        info.put( "officer", "2" );
        info.put( "guardType", "2" );
        info.put( "agent", profile );
        ext.put( "sender", info );

        //		if(!profile.equals("7706")){
        //			agent = "";
        //		}else{
        //			info.put( "agent", agent );
        //		}
        if ( !LiveCenterConfig.me.isLiveCenter() ) {
            info.put( "agent", agent );
        } else {
            agent = "";
        }

        try {
            long time = System.currentTimeMillis();
            ext.put( "systemtime", time );

            String data = info.get( "user_id" ).toString() + info.get( "nick_name" ).toString() + time + info.get( "user_level" )
                                                                                                             .toString() + text
                    + agent;
            ext.put( "userinfomat", RSACoder.signSha1Rsa( data, liveRsaPrivateKey ) );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }

        String value = JsonUtil.object2Json( ext );
        for ( LiveVideo liveVideo : liveVideoMapper.selectOnlineLiveGroups() ) {
            try {
                imApi.sendSystemNotify( liveVideo.getGroupId(), value );
                imServerUtils.sendGroupMessage( String.valueOf( liveVideo.getId() ), ext );
            } catch ( Exception e ) {
                log.error( "小助手发消息失败", e );
            }

        }
    }

    /**
     * 固定直播间发送消息
     *
     * @param text 文本
     */
    public void sendMsg( String text, String groupId, String videoId ) {
        if ( text == null ) {
            return;
        }
        String                  agent = profile;
        HashMap<String, Object> ext   = new HashMap<>();
        ext.put( "type", 0 ); //普通消息
        ext.put( "fonts_color", "" );
        ext.put( "text", text );
        Map<String, Object> info = new HashMap<>();
        info.put( "user_id", "admin" );
        info.put( "user_level", "50" );
        info.put( "nick_name", sysConfigCacheUtil.getConf( "77_help_nick_name" ) );
        info.put( "officer", "2" );
        info.put( "guardType", "2" );
        info.put( "agent", profile );
        ext.put( "sender", info );
        if ( !LiveCenterConfig.me.isLiveCenter() ) {
            info.put( "agent", agent );
        } else {
            agent = "";
        }
        try {
            long time = System.currentTimeMillis();
            ext.put( "systemtime", time );
            String data = info.get( "user_id" ).toString() + info.get( "nick_name" ).toString() + time + info.get( "user_level" )
                                                                                                             .toString() + text
                    + agent;
            log.error( "管理后台给主播发通知data:{}", data );
            ext.put( "userinfomat", RSACoder.signSha1Rsa( data, liveRsaPrivateKey ) );

            ext.put( "groupId", groupId );

            imApi.sendSystemNotify( groupId, JsonUtil.object2Json( ext ) );
            imServerUtils.sendGroupMessage( videoId, ext );
            log.warn( "小助手消息发送成功" + groupId, JsonUtil.object2Json( ext ) );
        } catch ( Exception e ) {
            log.error( "小助手发消息失败", e );
        }


    }
}
