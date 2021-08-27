package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ManageCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveBlack;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.mapper.LiveBlackMapper;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.service.ILiveBlackService;
import org.apache.logging.log4j.util.Strings;
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
        List<LiveBlack> liveBlackList = null;
        if (profile.equals("7706")) {
            List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList7706(liveBlack);
            List<LiveBlack> liveBlackList7706 = new ArrayList<>();
            for (LiveBlack liveBlack1 : liveBlackAllList) {
                if (liveBlack1.getBlackUserId().startsWith("7706")) {
                    liveBlackList7706.add(liveBlack1);
                }
            }
            return liveBlackList7706;
        } else if (profile.equals("7701")) {
            List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList(liveBlack);
            List<LiveBlack> liveBlackList7701 = new ArrayList<>();
            for (LiveBlack liveBlack1 : liveBlackAllList) {
                if (liveBlack1.getBlackUserId().startsWith("7701")) {
                    liveBlackList7701.add(liveBlack1);
                }
            }
            return liveBlackList7701;
        } else {
            liveBlackList = liveBlackMapper.selectLiveBlackList(liveBlack);
        }
        return liveBlackList;
    }

    @Override
    public AjaxResult deleteLiveBlackById(LiveBlack liveBlack) {
        int num;
        if (liveBlack.getBlackUserId().startsWith("7706")) {
            num = liveBlackMapper.deleteLiveBlackById7706(liveBlack.getId());
        } else {
            num = liveBlackMapper.deleteLiveBlackById(liveBlack.getId());
        }
        if (num <= 0) {
            return AjaxResult.error("移除黑名单失败");
        }
        manageCacheUtil.refreshBlack(liveBlack.getHostId());
        memberInfoMapper.updateSpeak(liveBlack.getBlackUserId(),0);
        return AjaxResult.success("移除黑名单成功");
    }


}