package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.LotteryCount;

import java.util.List;

public interface LotteryCountService {

    /**
     * 获取所有彩票计数 - Lottery Count list
     *
     * @param lotteryCount  - list of lotteryCount
     * @return 获取所有彩票计数 - list of Lottery Count
     */
    List<LotteryCount> selectAllLotteryCount(LotteryCount lotteryCount);
}
