package com.qiqilm.server.admin.imserver;

import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

@Log4j2
@Component
public class ImServerUtils {
    @Resource
    private RestTemplate restTemplate;

    @Value( "${im.sendGroupMsg:null}" )
    private String imSendGroupMsgUrl;
    @Value( "${im.sendMsg:null}" )
    private String imSendMsgUrl;

    /**
     * 发送在线群消息
     *
     * @param ext 消息map
     */
    @Async
    public void sendOnlineGroupMessage( Map<String, Object> ext ) {
        ext.put( "groupId", LiveCenterConfig.me.getLiveCenter() );
        ext.put( "uuid", UuidUtil.getRandomUuidWithoutSeparator() );
        RspBase<?> rspBase = this.sendGroupMessage( LiveCenterConfig.me.getLiveCenter(), ext, 3 );
        if ( rspBase != null && rspBase.getCode() == 200 ) {
            log.info( "新IM - 在线群组im消息发送成功" );
        }
    }

    /**
     * 发送群消息
     *
     * @param roomId 主播ID
     * @param ext    消息map
     */
    @Async
    public void sendGroupMessage( String roomId, Map<String, Object> ext ) {
        String groupId = LiveCenterConfig.me.getLiveCenter() + "@" + roomId.replaceAll( "#", "" ).replaceAll( "@", "" );
        // 设置群组ID
        ext.put( "groupId", groupId );
        ext.put( "uuid", UuidUtil.getRandomUuidWithoutSeparator() );
        RspBase<?> rspBase = this.sendGroupMessage( groupId, ext, 3 );
        if ( rspBase != null && rspBase.getCode() == 200 ) {
            log.info( "新IM - 群组{}im消息发送成功", groupId );
        }
    }

    private RspBase<?> sendGroupMessage( String groupId, Map<String, Object> messageMap, int retryNum ) {
        if ( StringUtils.isBlank( this.imSendGroupMsgUrl ) || !this.imSendGroupMsgUrl.startsWith( "http" ) ) {
            //log.error( "新IM - 未初始化参数, IM消息无法发送" );
            return null;
        }
        if ( retryNum <= 0 ) {
            log.error( "新IM - IM访问失败,message:{}", JsonUtil.object2Json( messageMap ) );
            return null;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( messageMap, httpHeaders );

        try {
            return restTemplate.postForObject( this.imSendGroupMsgUrl + groupId, httpEntity, RspBase.class );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        try {
            Thread.sleep( 999L );
        } catch ( InterruptedException ex ) {
            ex.printStackTrace();
        }
        retryNum--;
        return this.sendGroupMessage( groupId, messageMap, retryNum );
    }

    /**
     * 发送单会员消息
     *
     * @param memberId   会员ID
     * @param messageMap 消息map
     */
    public void sendMessage( String memberId, Map<String, Object> messageMap ) {
        if ( StringUtils.isBlank( this.imSendMsgUrl ) || !this.imSendMsgUrl.startsWith( "http" ) ) {
            //log.error( "新IM - 未初始化参数, IM消息无法发送" );
            return;
        }

        messageMap.put( "groupId", LiveCenterConfig.me.getLiveCenter() );
        messageMap.put( "uuid", UuidUtil.getRandomUuidWithoutSeparator() );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( messageMap, httpHeaders );

        try {
            RspBase<?> rspBase = restTemplate.postForObject( this.imSendMsgUrl + memberId, httpEntity, RspBase.class );
            if ( rspBase != null && rspBase.getCode() == 200 ) {
                log.info( "新IM - 单会员{}im消息发送成功", memberId );
                return;
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        log.error( "新IM - 单会员{}im消息发送失败", memberId );
    }
}
