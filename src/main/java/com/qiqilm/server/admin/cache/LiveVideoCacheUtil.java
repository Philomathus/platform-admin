package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.LiveProp;
import com.qiqilm.server.admin.domain.rsp.PropListRsp;
import com.qiqilm.server.admin.mapper.LivePropMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class LiveVideoCacheUtil {

    public static final String LIVE_VIDEO_KEY = Constants.LIVE_PREX + "liveProp:";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LivePropMapper livePropMapper;


    public void setLiveVideoCach(String type) {
        redisUtil.unlink(LIVE_VIDEO_KEY + type);
        List<PropListRsp> propList =livePropMapper.livePropList(type);
        redisUtil.lRightPushAll(LIVE_VIDEO_KEY+type,
                propList.stream().map(JsonUtil::object2Json).collect(Collectors.toList()));
    }
}
