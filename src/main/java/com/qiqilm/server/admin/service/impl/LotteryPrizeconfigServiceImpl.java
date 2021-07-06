package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.LotteryPrizeconfig;
import com.qiqilm.server.admin.mapper.LotteryPrizeconfigMapper;
import com.qiqilm.server.admin.service.ILotteryPrizeconfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 开奖配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-18
 */
@Service
public class LotteryPrizeconfigServiceImpl implements ILotteryPrizeconfigService {
    @Autowired
    private LotteryPrizeconfigMapper lotteryPrizeconfigMapper;

    /**
     * 查询开奖配置
     *
     * @param lotteryId 开奖配置ID
     * @return 开奖配置
     */
    @Override
    public LotteryPrizeconfig selectLotteryPrizeconfigById(String lotteryId) {
        return lotteryPrizeconfigMapper.selectLotteryPrizeconfigById(lotteryId);
    }

    /**
     * 查询开奖配置列表
     *
     * @param lotteryPrizeconfig 开奖配置
     * @return 开奖配置
     */
    @Override
    public List<LotteryPrizeconfig> selectLotteryPrizeconfigList(LotteryPrizeconfig lotteryPrizeconfig) {
        return lotteryPrizeconfigMapper.selectLotteryPrizeconfigList(lotteryPrizeconfig);
    }

    /**
     * 新增开奖配置
     *
     * @param lotteryPrizeconfig 开奖配置
     * @return 结果
     */
    @Override
    public int insertLotteryPrizeconfig(LotteryPrizeconfig lotteryPrizeconfig) {
        return lotteryPrizeconfigMapper.insertLotteryPrizeconfig(lotteryPrizeconfig);
    }

    /**
     * 修改开奖配置
     *
     * @param lotteryPrizeconfig 开奖配置
     * @return 结果
     */
    @Override
    public int updateLotteryPrizeconfig(LotteryPrizeconfig lotteryPrizeconfig) {
        return lotteryPrizeconfigMapper.updateLotteryPrizeconfig(lotteryPrizeconfig);
    }

    /**
     * 批量删除开奖配置
     *
     * @param lotteryIds 需要删除的开奖配置ID
     * @return 结果
     */
    @Override
    public int deleteLotteryPrizeconfigByIds(String[] lotteryIds) {
        return lotteryPrizeconfigMapper.deleteLotteryPrizeconfigByIds(lotteryIds);
    }

    /**
     * 删除开奖配置信息
     *
     * @param lotteryId 开奖配置ID
     * @return 结果
     */
    @Override
    public int deleteLotteryPrizeconfigById(String lotteryId) {
        return lotteryPrizeconfigMapper.deleteLotteryPrizeconfigById(lotteryId);
    }
}
