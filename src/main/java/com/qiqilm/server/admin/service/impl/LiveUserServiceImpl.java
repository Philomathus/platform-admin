package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.RedisCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveFamily;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;
import com.qiqilm.server.admin.mapper.LiveFamilyMapper;
import com.qiqilm.server.admin.mapper.LiveUserMapper;
import com.qiqilm.server.admin.service.ILiveUserService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 主播用户信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveUserServiceImpl implements ILiveUserService {
    @Autowired
    private LiveUserMapper liveUserMapper;
    @Autowired
    private LiveFamilyMapper liveFamilyMapper;

    /**
     * 查询主播用户信息
     *
     * @param id 主播用户信息ID
     * @return 主播用户信息
     */
    @Override
    public LiveUser selectLiveUserById(Long id) {
        return liveUserMapper.selectLiveUserById(id);
    }

    /**
     * 查询主播用户信息列表
     *
     * @param liveUser 主播用户信息
     * @return 主播用户信息
     */
    @Override
    public List<LiveUser> selectLiveUserList(LiveUser liveUser) {
        return liveUserMapper.selectLiveUserList(liveUser);
    }

    /**
     * 新增主播用户信息
     *
     * @param liveUser 主播用户信息
     * @return 结果
     */
    @Override
    public int insertLiveUser(LiveUser liveUser) {
        liveUser.setCreateTime(DateUtils.getNowDate());
        return liveUserMapper.insertLiveUser(liveUser);
    }

    /**
     * 修改主播用户信息
     *
     * @param liveUser 主播用户信息
     * @return 结果
     */
    @Override
    public int updateLiveUser(LiveUser liveUser) {
        liveUser.setUpdateTime(DateUtils.getNowDate());
        return liveUserMapper.updateLiveUser(liveUser);
    }

    /**
     * 批量删除主播用户信息
     *
     * @param ids 需要删除的主播用户信息ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserByIds(Long[] ids) {
        return liveUserMapper.deleteLiveUserByIds(ids);
    }

    /**
     * 删除主播用户信息信息
     *
     * @param id 主播用户信息ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserById(Long id) {
        return liveUserMapper.deleteLiveUserById(id);
    }

    @Override
    public AjaxResult updateFamilyID(Long familyID, Long userId) {
        LiveFamily liveFamily = liveFamilyMapper.selectLiveFamilyById(familyID);
        if (liveFamily != null || familyID == 0) {
            if (familyID == 0) {
                int oldFamilyId = liveUserMapper.getFamilyId(userId);
                int i = liveUserMapper.updateFamilyID(familyID, userId);
                int num = liveUserMapper.getNumFamily(oldFamilyId);
                liveFamilyMapper.updateFamilyID(num, oldFamilyId);
            } else {
                int oldFamilyId = liveUserMapper.getFamilyId(userId);
                int i = liveUserMapper.updateFamilyID(familyID, userId);
                int num = liveUserMapper.getNumFamily(oldFamilyId);
                liveFamilyMapper.updateFamilyID(num, oldFamilyId);
                int newnum = liveUserMapper.getNumFamily(familyID.intValue());
                liveFamilyMapper.updateFamilyID(newnum, familyID.intValue());
            }
            RedisCacheUtil.me.clear(userId, LiveUser.class);
            return AjaxResult.success();

        }
        return AjaxResult.error();
    }

    @Override
    public List<RspLotteryBet> selectAnchorAward( ReqLotteryBat req) {
        List<RspLotteryBet> lotteryBets = liveUserMapper.selectAnchorAward(req);
        for (RspLotteryBet lotteryBet : lotteryBets) {
            BigDecimal subtract = lotteryBet.getPrize().subtract(lotteryBet.getCost());
            if (subtract.compareTo(BigDecimal.ZERO) > 0) {
                lotteryBet.setPrizeSixThousand(subtract.multiply(BigDecimal.valueOf(0.006)).setScale(2, BigDecimal.ROUND_HALF_UP));
            } else {
                lotteryBet.setPrizeSixThousand(BigDecimal.ZERO);
            }
        }
        return lotteryBets;
    }
}
