package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LotteryHistoryMapper;
import com.qiqilm.server.admin.domain.LotteryHistory;
import com.qiqilm.server.admin.service.ILotteryHistoryService;

/**
 * 开奖历史Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-23
 */
@Service
public class LotteryHistoryServiceImpl implements ILotteryHistoryService {
    @Autowired
    private LotteryHistoryMapper lotteryHistoryMapper;

    /**
     * 查询开奖历史列表
     *
     * @param lotteryHistory 开奖历史
     * @return 开奖历史
     */
    @Override
    public List<LotteryHistory> selectLotteryHistoryList(LotteryHistory lotteryHistory) {
        return lotteryHistoryMapper.selectLotteryHistoryList(lotteryHistory);
    }

    /**
     * 查询全部彩种
     *
     * @param lotteryHistoryName 全部彩种
     * @return 全部彩种
     */
    @Override
    public List<LotteryHistory> selectLotteryHistoryList() {
        return lotteryHistoryMapper.selectLotteryHistoryNameList();
    }

    @Override
    public String selectKtimeById(String id) {
        return lotteryHistoryMapper.selectKtimeById(id);
    }

    @Override
    public int changeStatus(String id) {
        return lotteryHistoryMapper.changeStatus(id);
    }
}
