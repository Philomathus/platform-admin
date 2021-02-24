package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryTemp;

/**
 * 彩票即时信息Service接口
 *
 * @author 77tv
 * @date 2021-02-23
 */
public interface ILotteryTempService {

	/**
	 * 查询彩票即时信息列表
	 *
	 * @param lotteryTemp 彩票即时信息
	 * @return 彩票即时信息集合
	 */
	public List<LotteryTemp> selectLotteryTempList(LotteryTemp lotteryTemp);
}
