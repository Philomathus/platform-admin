package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LotteryCount;

import java.util.List;

/**
 *计数列表 Mapper -  lottery count mapper
 *
 * @author rajesh
 * @date 2022-09-30
 */
public interface LotteryCountMapper {

	/**
	 * 获取所有彩票计数 - Lottery Count list
	 *
	 * @param lotteryCount  - list of lotteryCount
	 * @return 获取所有彩票计数 - list of Lottery Count
	 */
	 List<LotteryCount> selectAllLotteryCount(LotteryCount lotteryCount);

}
