package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryInfo;

/**
 * 彩票名称Service接口
 *
 * @author 77tv
 * @date 2021-02-23
 */
public interface ILotteryInfoService {

	/**
	 * 查询彩票名称列表
	 *
	 * @param lotteryInfo 彩票名称
	 * @return 彩票名称集合
	 */
	public List<LotteryInfo> selectLotteryInfoList(LotteryInfo lotteryInfo);

}
