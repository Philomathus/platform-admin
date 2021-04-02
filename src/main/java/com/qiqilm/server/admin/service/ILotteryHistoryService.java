package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryHistory;

/**
 * 开奖历史Service接口
 *
 * @author 77tv
 * @date 2021-02-23
 */
public interface ILotteryHistoryService {
	/**
	 * 查询开奖历史列表
	 *
	 * @param lotteryHistory 开奖历史
	 * @return 开奖历史集合
	 */
	public List<LotteryHistory> selectLotteryHistoryList(LotteryHistory lotteryHistory);

	/**
	 * 查询全部彩种
	 *
	 * @param lotteryHistory 开奖历史
	 * @return 开奖历史集合
	 */
	public List<LotteryHistory> selectLotteryHistoryList();

	/**
	 * 重新派奖
	 *
	 * @param lotteryHistory 开奖历史
	 * @return 开奖历史集合
	 */
	public String selectKtimeById(String id);

	int changeStatus(String id);
}
