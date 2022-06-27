package com.qiqilm.server.admin.utils;

import com.qiqilm.server.admin.cache.BatchIMCacheUtil;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.im.MessageEnum;
import com.qiqilm.server.admin.im.MessageType;
import com.qiqilm.server.admin.service.ILiveVideoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private ILiveVideoService liveVideoService;
    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;
    @Autowired
    private BatchIMCacheUtil batchIMCacheUtil;

    @Value("${live.encrypt.privateKey}")
    private String liveRsaPrivateKey;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private ImApi imApi;

    /**
     * 所有直播间发送消息
     *
     * @param text 文本
     */
    public void sendMsg(String text) {
        if (text == null) {
            return;
        }
        String agent = profile;
        HashMap<String, Object> ext = new HashMap<>();
        ext.put("type", 0); //普通消息
        ext.put("fonts_color", "");
        ext.put("text", text);
        Map<String, Object> info = new HashMap<>();
        info.put("user_id", "admin");
        info.put("user_level", "50");
        info.put("nick_name", sysConfigCacheUtil.getConf("77_help_nick_name"));
        info.put("officer", "2");
        info.put("guardType", "2");
        info.put("agent", profile);
        ext.put("sender", info);

//		if(!profile.equals("7706")){
//			agent = "";
//		}else{
//			info.put( "agent", agent );
//		}
        if (profile.equals("7706") || profile.equals("7705") || profile.equals("7710") || profile.equals("7711")
                || profile.equals("77mm") || profile.equals("77jp") || profile.equals("7703")) {
            info.put("agent", agent);
        } else {
            agent = "";
        }

        try {
            long time = System.currentTimeMillis();
            ext.put("systemtime", time);

            String data = info.get("user_id").toString() + info.get("nick_name").toString() + time
                    + info.get("user_level").toString() + text + agent;
            ext.put("userinfomat", RSA8SignUtils.sign(data, liveRsaPrivateKey));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        for (String groupId : liveVideoService.selectOnlineLiveGroups()) {
            try {
                // ext.put("groupId", groupId);
                // batchIMCacheUtil.push(JsonUtil.object2Json(ext));

                imApi.sendSystemNotify(groupId, JsonUtil.object2Json(ext));
            } catch (Exception e) {
                log.error("小助手发消息失败", e);
            }

        }
    }

    /**
     * 固定直播间发送消息
     *
     * @param text 文本
     */
    public void sendMsg(String text, String groupId) {
        if (text == null) {
            return;
        }
        String agent = profile;
        HashMap<String, Object> ext = new HashMap<>();
        ext.put("type", 0); //普通消息
        ext.put("fonts_color", "");
        ext.put("text", text);
        Map<String, Object> info = new HashMap<>();
        info.put("user_id", "admin");
        info.put("user_level", "50");
        info.put("nick_name", sysConfigCacheUtil.getConf("77_help_nick_name"));
        info.put("officer", "2");
        info.put("guardType", "2");
        info.put("agent", profile);
        ext.put("sender", info);
        if (profile.equals("7706") || profile.equals("7705") || profile.equals("7710") || profile.equals("7711")
                || profile.equals("77mm") || profile.equals("77jp") || profile.equals("7703")) {
            info.put("agent", agent);
        } else {
            agent = "";
        }

        try {
            long time = System.currentTimeMillis();
            ext.put("systemtime", time);
            String data = info.get("user_id").toString() + info.get("nick_name").toString() + time
                    + info.get("user_level").toString() + text + agent;
            log.error("管理后台给主播发通知data:{}", data);
            ext.put("userinfomat", RSA8SignUtils.sign(data, liveRsaPrivateKey));

            ext.put("groupId", groupId);

            imApi.sendSystemNotify(groupId, JsonUtil.object2Json(ext));

            //batchIMCacheUtil.push(JsonUtil.object2Json(ext));

            //MessageType messageType = MessageType.setMsgEnmu(MessageEnum.TIMCustomElem).setData(JsonUtil.object2Json(ext));
            //imApi.sendGroupMessage(groupId, messageType);
            log.warn("小助手消息发送成功" + groupId, JsonUtil.object2Json(ext));
        } catch (Exception e) {
            log.error("小助手发消息失败", e);
        }


    }
}
