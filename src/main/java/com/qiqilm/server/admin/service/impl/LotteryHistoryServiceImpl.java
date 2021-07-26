package com.qiqilm.server.admin.service.impl;

import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.utils.PageUtil;
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


    /**
     * 1.校验数据正确性
     * 2.剔除重复数据
     * 3.批量插入数据库
     * @param lotteryHistory
     * @return
     */
    @Override
    public int batchLotteryHistory(List<LotteryHistory> lotteryHistory) {
        List<List<?>> insertBatchList = PageUtil.pageList(lotteryHistory,1000);//默认1000条数据
        for(List<?> list: insertBatchList){
            lotteryHistoryMapper.batchLotteryHistoryList((List<LotteryHistory>) list);
        }
        return lotteryHistory.size();
    }

    /**
     * 如果批量数据多的话，需要分页处理
     * @param startTime
     * @param endTime
     * @param lotteryId
     * @return
     */
    @Override
    public List<LotteryHistory> selectBetweenByTime(Date startTime, Date endTime, Long lotteryId) {
        return lotteryHistoryMapper.selectBetweenByTimeList(startTime,endTime,lotteryId);
    }
}
