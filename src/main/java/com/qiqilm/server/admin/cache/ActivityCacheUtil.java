package com.qiqilm.server.admin.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.ActivityInfo;
import com.qiqilm.server.admin.domain.ActivityQuestInfo;
import com.qiqilm.server.admin.domain.ActivityQuestType;
import com.qiqilm.server.admin.domain.ActivityType;
import com.qiqilm.server.admin.mapper.ActivityInfoMapper;
import com.qiqilm.server.admin.mapper.ActivityQuestInfoMapper;
import com.qiqilm.server.admin.mapper.ActivityQuestTypeMapper;
import com.qiqilm.server.admin.mapper.ActivityTypeMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class ActivityCacheUtil {
    // 活动类型
    public static final String ACTIVITY_TYPE_KEY = Constants.LIVE_PREX + "activity:activityType";
    public static final String ACTIVITY_INFO_KEY = Constants.LIVE_PREX + "activity:activityInfo";
    //任务
    public static final String ACTIVITY_QUEST_INFO_KEY = Constants.LIVE_PREX + "activity:activityQuestInfo";
    public static final String ACTIVITY_QUEST_TYPE_KEY = Constants.LIVE_PREX + "activity:activityQuestType";
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private ActivityInfoMapper activityInfoMapper;
    @Resource
    private ActivityTypeMapper activityTypeMapper;
    @Resource
    private ActivityQuestInfoMapper activityQuestInfoMapper;
    @Resource
    private ActivityQuestTypeMapper activityQuestTypeMapper;


    /**
     * 活动信息
     */
    public void addActivityInfo(ActivityInfo activityInfo) {
        String key = ACTIVITY_INFO_KEY;
        redisUtil.lRightPushAll(key, JsonUtil.object2Json(activityInfo));
    }

    /**
     * 活动类型
     */
    public void addActivityType(ActivityType activityType) {
        String key = ACTIVITY_TYPE_KEY;
        redisUtil.lRightPushAll(key, JsonUtil.object2Json(activityType));
    }

    /**
     * 任务信息
     */
    public void activityQuestInfo(ActivityQuestInfo activityQuestInfo) {
        String key = ACTIVITY_QUEST_INFO_KEY;
        redisUtil.lRightPushAll(key, JsonUtil.object2Json(activityQuestInfo));
    }

    /**
     * 任务类型
     */
    public void activityQuestType(ActivityQuestType activityQuestType) {
        String key = ACTIVITY_QUEST_TYPE_KEY;
        redisUtil.lRightPushAll(key, JsonUtil.object2Json(activityQuestType));
    }

    /**
     * 清空緩存
     */
    public void delActiveCache(String key) {
        redisUtil.unlink(key);
    }

    /**
     * 查询活动信息列表
     */
    public List<ActivityInfo> getActiveInfos() {
        //判断是否有缓存
        Boolean exists = redisUtil.exists(ACTIVITY_INFO_KEY);
        if (exists == null || !exists) {
            ActivityInfo activityInfo = new ActivityInfo();
            List<ActivityInfo> activityInfos = activityInfoMapper.selectActivityInfoList(activityInfo);
            if (activityInfos.size()>0 && activityInfos!=null){
                redisUtil.lRightPushAll(ACTIVITY_INFO_KEY,
                        activityInfos.stream().map(JsonUtil::object2Json).collect(Collectors.toList()));
            }
            return activityInfos;
        }
        List<String> list = redisUtil.lRange(ACTIVITY_INFO_KEY, 0, -1);
        ArrayList<ActivityInfo> activityInfos = JsonUtil.json2Object(list.toString(), new TypeReference<ArrayList<ActivityInfo>>() {
        });
        Collections.reverse(activityInfos);
        return activityInfos;
    }

    /**
     * 查询活动类型列表
     */
    public List<ActivityType> getActiveTypes() {
        //判断是否有缓存
        Boolean exists = redisUtil.exists(ACTIVITY_TYPE_KEY);
        if (exists == null || !exists) {
            ActivityType activityType = new ActivityType();
            List<ActivityType> activityInfos = activityTypeMapper.selectActivityTypeList(activityType);
            if (activityInfos.size()>0 && activityInfos!=null){
                redisUtil.lRightPushAll(ACTIVITY_TYPE_KEY,
                        activityInfos.stream().map(JsonUtil::object2Json).collect(Collectors.toList()));
            }
            return activityInfos;
        }
        List<String> list = redisUtil.lRange(ACTIVITY_TYPE_KEY, 0, -1);
        ArrayList<ActivityType> activityInfos = JsonUtil.json2Object(list.toString(), new TypeReference<ArrayList<ActivityType>>() {
        });
        Collections.reverse(activityInfos);
        return activityInfos;
    }

    /**
     * 查询任务信息列表
     */
    public List<ActivityQuestInfo> getQuestInfos() {
        //判断是否有缓存
        Boolean exists = redisUtil.exists(ACTIVITY_QUEST_INFO_KEY);
        if (exists == null || !exists) {
            ActivityQuestInfo activityQuestInfo=new ActivityQuestInfo();
            List<ActivityQuestInfo> activityQuestInfos = activityQuestInfoMapper.selectActivityQuestInfoList(activityQuestInfo);
            if (activityQuestInfos.size()>0 && activityQuestInfos!=null){
                redisUtil.lRightPushAll(ACTIVITY_TYPE_KEY,
                        activityQuestInfos.stream().map(JsonUtil::object2Json).collect(Collectors.toList()));
            }
            return activityQuestInfos;
        }
        List<String> list = redisUtil.lRange(ACTIVITY_QUEST_INFO_KEY, 0, -1);
        ArrayList<ActivityQuestInfo> activityInfos = JsonUtil.json2Object(list.toString(), new TypeReference<ArrayList<ActivityQuestInfo>>() {
        });
        Collections.reverse(activityInfos);
        return activityInfos;
    }

    /**
     * 查询任务类型列表
     */
    public List<ActivityQuestType> getQuestTypes() {
        //判断是否有缓存
        Boolean exists = redisUtil.exists(ACTIVITY_QUEST_TYPE_KEY);
        if (exists == null || !exists) {
            ActivityQuestType activityQuestType=new ActivityQuestType();
            List<ActivityQuestType> activityQuestTypes = activityQuestTypeMapper.selectActivityQuestTypeList(activityQuestType);
            if (activityQuestTypes.size()>0 && activityQuestTypes!=null){
                redisUtil.lRightPushAll(ACTIVITY_TYPE_KEY,
                        activityQuestTypes.stream().map(JsonUtil::object2Json).collect(Collectors.toList()));
            }
            return activityQuestTypes;
        }
        List<String> list = redisUtil.lRange(ACTIVITY_QUEST_TYPE_KEY, 0, -1);
        ArrayList<ActivityQuestType> activityInfos = JsonUtil.json2Object(list.toString(), new TypeReference<ArrayList<ActivityQuestType>>() {
        });
        Collections.reverse(activityInfos);
        return activityInfos;
    }


}
