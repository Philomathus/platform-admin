package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ManageCacheUtil;
import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveBlack;
import com.qiqilm.server.admin.mapper.LiveBlackMapper;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.service.ILiveBlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 拉黑Service业务层处理
 *
 * @author 77tv
 * @date 2021-08-24
 */
@Service
public class LiveBlackServiceImpl implements ILiveBlackService {
    @Autowired
    private LiveBlackMapper liveBlackMapper;
    @Value("${spring.profiles.active}")
    private String profile;
    @Autowired
    private ManageCacheUtil manageCacheUtil;
    @Autowired
    private MemberInfoMapper memberInfoMapper;

    /**
     * 查询拉黑列表
     *
     * @param liveBlack 拉黑
     * @return 拉黑
     */
    @Override
    public List<LiveBlack> selectLiveBlackList(LiveBlack liveBlack) {
        List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList(liveBlack, LiveCenterConfig.me.getLiveCenterDbLive());
        List<LiveBlack> liveBlackList = new ArrayList<>();
        for (LiveBlack liveBlack1 : liveBlackAllList) {
            if (liveBlack1.getBlackUserId().startsWith(profile)) {
                liveBlackList.add(liveBlack1);
            }
        }
        return liveBlackList;
    }

    @Override
    public AjaxResult deleteLiveBlackById(LiveBlack liveBlack) {
        int num = liveBlackMapper.deleteLiveBlackById(liveBlack.getId(), LiveCenterConfig.me.getLiveCenterDbLive());
        if (num <= 0) {
            return AjaxResult.error("移除黑名单失败");
        }
        manageCacheUtil.addBlackUser(liveBlack.getHostId(), liveBlack.getBlackUserId());
        memberInfoMapper.updateSpeak(liveBlack.getBlackUserId(), 0);
        return AjaxResult.success("移除黑名单成功");
    }


}