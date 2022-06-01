package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ManageCacheUtil;
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
        List<LiveBlack> liveBlackList = null;
        switch (profile) {
            case "7706": {
                List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList7706(liveBlack);
                List<LiveBlack> liveBlackList7706 = new ArrayList<>();
                for (LiveBlack liveBlack1 : liveBlackAllList) {
                    if (liveBlack1.getBlackUserId().startsWith("7706")) {
                        liveBlackList7706.add(liveBlack1);
                    }
                }
                return liveBlackList7706;
            }
            case "7705": {
                List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList7705(liveBlack);
                List<LiveBlack> liveBlackList7705 = new ArrayList<>();
                for (LiveBlack liveBlack1 : liveBlackAllList) {
                    if (liveBlack1.getBlackUserId().startsWith("7705")) {
                        liveBlackList7705.add(liveBlack1);
                    }
                }
                return liveBlackList7705;
            }
            case "7710": {
                List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList7710(liveBlack);
                List<LiveBlack> liveBlackList7710 = new ArrayList<>();
                for (LiveBlack liveBlack1 : liveBlackAllList) {
                    if (liveBlack1.getBlackUserId().startsWith("7710")) {
                        liveBlackList7710.add(liveBlack1);
                    }
                }
                return liveBlackList7710;
            }
            case "7701": {
                List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList(liveBlack);
                List<LiveBlack> liveBlackList7701 = new ArrayList<>();
                for (LiveBlack liveBlack1 : liveBlackAllList) {
                    if (liveBlack1.getBlackUserId().startsWith("7701")) {
                        liveBlackList7701.add(liveBlack1);
                    }
                }
                return liveBlackList7701;
            }
            case "7704": {
                List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList(liveBlack);
                List<LiveBlack> liveBlackList7704 = new ArrayList<>();
                for (LiveBlack liveBlack1 : liveBlackAllList) {
                    if (liveBlack1.getBlackUserId().startsWith("7704")) {
                        liveBlackList7704.add(liveBlack1);
                    }
                }
                return liveBlackList7704;
            }
            case "7708": {
                List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList(liveBlack);
                List<LiveBlack> liveBlackList7708 = new ArrayList<>();
                for (LiveBlack liveBlack1 : liveBlackAllList) {
                    if (liveBlack1.getBlackUserId().startsWith("7708")) {
                        liveBlackList7708.add(liveBlack1);
                    }
                }
                return liveBlackList7708;
            }
            case "7711": {
                List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList7711(liveBlack);
                List<LiveBlack> liveBlackList7711 = new ArrayList<>();
                for (LiveBlack liveBlack1 : liveBlackAllList) {
                    if (liveBlack1.getBlackUserId().startsWith("7711")) {
                        liveBlackList7711.add(liveBlack1);
                    }
                }
                return liveBlackList7711;
            }
            case "7712": {
                List<LiveBlack> liveBlackAllList = liveBlackMapper.selectLiveBlackList7712(liveBlack);
                List<LiveBlack> liveBlackList7712 = new ArrayList<>();
                for (LiveBlack liveBlack1 : liveBlackAllList) {
                    if (liveBlack1.getBlackUserId().startsWith("7712")) {
                        liveBlackList7712.add(liveBlack1);
                    }
                }
                return liveBlackList7712;
            }
            default:
                liveBlackList = liveBlackMapper.selectLiveBlackList(liveBlack);
                break;
        }
        return liveBlackList;
    }

    @Override
    public AjaxResult deleteLiveBlackById(LiveBlack liveBlack) {
        int num;
        if (liveBlack.getBlackUserId().startsWith("7706") || liveBlack.getBlackUserId().startsWith("7711")) {
            num = liveBlackMapper.deleteLiveBlackById7706(liveBlack.getId());
        } else if (liveBlack.getBlackUserId().startsWith("7705") || liveBlack.getBlackUserId().startsWith("7712")) {
            num = liveBlackMapper.deleteLiveBlackById7705(liveBlack.getId());
        } else if (liveBlack.getBlackUserId().startsWith("7710")) {
            num = liveBlackMapper.deleteLiveBlackById7710(liveBlack.getId());
        } else {
            num = liveBlackMapper.deleteLiveBlackById(liveBlack.getId());
        }
        if (num <= 0) {
            return AjaxResult.error("移除黑名单失败");
        }
        manageCacheUtil.refreshBlack(liveBlack.getHostId());
        memberInfoMapper.updateSpeak(liveBlack.getBlackUserId(), 0);
        return AjaxResult.success("移除黑名单成功");
    }


}