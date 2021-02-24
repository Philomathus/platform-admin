package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LotteryGameMapper;
import com.qiqilm.server.admin.domain.LotteryGame;
import com.qiqilm.server.admin.service.ILotteryGameService;

/**
 * 下注Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-23
 */
@Service
public class LotteryGameServiceImpl implements ILotteryGameService {
    @Autowired
    private LotteryGameMapper lotteryGameMapper;

    /**
     * 查询下注
     *
     * @param id 下注ID
     * @return 下注
     */
    @Override
    public LotteryGame selectLotteryGameById(String id) {
        return lotteryGameMapper.selectLotteryGameById(id);
    }

    /**
     * 查询下注列表
     *
     * @param lotteryGame 下注
     * @return 下注
     */
    @Override
    public List<LotteryGame> selectLotteryGameList(LotteryGame lotteryGame) {
        return lotteryGameMapper.selectLotteryGameList(lotteryGame);
    }

    /**
     * 修改下注
     *
     * @param lotteryGame 下注
     * @return 结果
     */
    @Override
    public int updateLotteryGame(LotteryGame lotteryGame) {
        return lotteryGameMapper.updateLotteryGame(lotteryGame);
    }

}
