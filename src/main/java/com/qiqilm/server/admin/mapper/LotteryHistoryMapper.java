package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryHistory;

/**
 * 开奖历史Mapper接口
 *
 * @author 77tv
 * @date 2021-02-23
 */
public interface LotteryHistoryMapper {

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
	 * @return 全部彩种集合
	 */
	public List<LotteryHistory> selectLotteryHistoryNameList();

}
