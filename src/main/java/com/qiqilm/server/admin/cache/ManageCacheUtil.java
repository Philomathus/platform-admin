package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.mapper.LiveBlackMapper;
import com.qiqilm.server.admin.mapper.LiveOfficerMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class ManageCacheUtil {
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private LiveBlackMapper liveBlackMapper;
    @Resource
    private LiveOfficerMapper liveOfficerMapper;
    public static final String LIVE_BLACK_KEY = Constants.LIVE_PREX + "liveBlack:";

    public static final String MANAGE_KEY = Constants.LIVE_PREX + "manage:";

    public Set<String> refreshManage(Long host_id) {
        // 第一步 判断这个key是否存在
        redisUtil.unlink(MANAGE_KEY + host_id);
        Set<String> set = liveOfficerMapper.userManage(host_id);
        if (!set.isEmpty()) {
            redisUtil.sAdd(MANAGE_KEY + host_id, set.toArray(new String[0]));
        }
        return set;
    }

    public Set<String> refreshBlack(Long host_id) {
        redisUtil.unlink(LIVE_BLACK_KEY + host_id);
        Set<String> set = liveBlackMapper.userBlackList(host_id);
        if (!set.isEmpty()) {
            redisUtil.sAdd(LIVE_BLACK_KEY + host_id, set.toArray(new String[0]));
        }
        return set;
    }

    public Long addBlackUser(Long host_id, String userId) {
        if (redisUtil.sSize(LIVE_BLACK_KEY + host_id) == 1 && redisUtil.sRandom(LIVE_BLACK_KEY + host_id).startsWith("[")) {
            refreshBlack(host_id);
        }
        return redisUtil.sAdd(LIVE_BLACK_KEY + host_id, userId);
    }

    public Long addManage(Long host_id, String userId) {
        if (redisUtil.sSize(MANAGE_KEY + host_id) == 1 && redisUtil.sRandom(MANAGE_KEY + host_id).startsWith("[")) {
            refreshManage(host_id);
        }
        return redisUtil.sAdd(MANAGE_KEY + host_id, userId);
    }

    public Long removeManage(Long host_id, String userId) {
        if (redisUtil.sSize(MANAGE_KEY + host_id) == 1 && redisUtil.sRandom(MANAGE_KEY + host_id).startsWith("[")) {
            refreshManage(host_id);
        }
        return redisUtil.sRemove(MANAGE_KEY + host_id, userId);
    }
}
