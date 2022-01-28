package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LotteryPrizeMapper;
import com.qiqilm.server.admin.domain.LotteryPrize;
import com.qiqilm.server.admin.service.ILotteryPrizeService;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2022-01-27
 */
@Service
public class LotteryPrizeServiceImpl implements ILotteryPrizeService {
    @Resource
    private LotteryPrizeMapper lotteryPrizeMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LotteryPrize selectLotteryPrizeById(Long id) {
        return lotteryPrizeMapper.selectLotteryPrizeById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param lotteryPrize 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<LotteryPrize> selectLotteryPrizeList(LotteryPrize lotteryPrize) {
        return lotteryPrizeMapper.selectLotteryPrizeList(lotteryPrize);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param lotteryPrize 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertLotteryPrize(LotteryPrize lotteryPrize) {
        return lotteryPrizeMapper.insertLotteryPrize(lotteryPrize);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param lotteryPrize 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateLotteryPrize(LotteryPrize lotteryPrize) {
        return lotteryPrizeMapper.updateLotteryPrize(lotteryPrize);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLotteryPrizeByIds(Long[] ids) {
        return lotteryPrizeMapper.deleteLotteryPrizeByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLotteryPrizeById(Long id) {
        return lotteryPrizeMapper.deleteLotteryPrizeById(id);
    }
}
