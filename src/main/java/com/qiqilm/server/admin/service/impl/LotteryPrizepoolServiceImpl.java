package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.LotteryPrizepool;
import com.qiqilm.server.admin.mapper.LotteryPrizepoolMapper;
import com.qiqilm.server.admin.service.ILotteryPrizepoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 奖池配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-18
 */
@Service
public class LotteryPrizepoolServiceImpl implements ILotteryPrizepoolService {
    @Autowired
    private LotteryPrizepoolMapper lotteryPrizepoolMapper;

    /**
     * 查询奖池配置
     *
     * @param id 奖池配置ID
     * @return 奖池配置
     */
    @Override
    public LotteryPrizepool selectLotteryPrizepoolById(String id) {
        return lotteryPrizepoolMapper.selectLotteryPrizepoolById(id);
    }

    /**
     * 查询奖池配置列表
     *
     * @param lotteryPrizepool 奖池配置
     * @return 奖池配置
     */
    @Override
    public List<LotteryPrizepool> selectLotteryPrizepoolList(LotteryPrizepool lotteryPrizepool) {
        return lotteryPrizepoolMapper.selectLotteryPrizepoolList(lotteryPrizepool);
    }

    /**
     * 新增奖池配置
     *
     * @param lotteryPrizepool 奖池配置
     * @return 结果
     */
    @Override
    public int insertLotteryPrizepool(LotteryPrizepool lotteryPrizepool) {
        return lotteryPrizepoolMapper.insertLotteryPrizepool(lotteryPrizepool);
    }

    /**
     * 修改奖池配置
     *
     * @param lotteryPrizepool 奖池配置
     * @return 结果
     */
    @Override
    public int updateLotteryPrizepool(LotteryPrizepool lotteryPrizepool) {
        return lotteryPrizepoolMapper.updateLotteryPrizepool(lotteryPrizepool);
    }

    /**
     * 批量删除奖池配置
     *
     * @param ids 需要删除的奖池配置ID
     * @return 结果
     */
    @Override
    public int deleteLotteryPrizepoolByIds(String[] ids) {
        return lotteryPrizepoolMapper.deleteLotteryPrizepoolByIds(ids);
    }

    /**
     * 删除奖池配置信息
     *
     * @param id 奖池配置ID
     * @return 结果
     */
    @Override
    public int deleteLotteryPrizepoolById(String id) {
        return lotteryPrizepoolMapper.deleteLotteryPrizepoolById(id);
    }
}
