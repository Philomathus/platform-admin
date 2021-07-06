package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.LotteryPrizeconfig;

import java.util.List;

/**
 * 开奖配置Service接口
 *
 * @author 77tv
 * @date 2021-03-18
 */
public interface ILotteryPrizeconfigService {
	/**
	 * 查询开奖配置
	 *
	 * @param lotteryId 开奖配置ID
	 * @return 开奖配置
	 */
	public LotteryPrizeconfig selectLotteryPrizeconfigById(String lotteryId);

	/**
	 * 查询开奖配置列表
	 *
	 * @param lotteryPrizeconfig 开奖配置
	 * @return 开奖配置集合
	 */
	public List<LotteryPrizeconfig> selectLotteryPrizeconfigList(LotteryPrizeconfig lotteryPrizeconfig);

	/**
	 * 新增开奖配置
	 *
	 * @param lotteryPrizeconfig 开奖配置
	 * @return 结果
	 */
	public int insertLotteryPrizeconfig(LotteryPrizeconfig lotteryPrizeconfig);

	/**
	 * 修改开奖配置
	 *
	 * @param lotteryPrizeconfig 开奖配置
	 * @return 结果
	 */
	public int updateLotteryPrizeconfig(LotteryPrizeconfig lotteryPrizeconfig);

	/**
	 * 批量删除开奖配置
	 *
	 * @param lotteryIds 需要删除的开奖配置ID
	 * @return 结果
	 */
	public int deleteLotteryPrizeconfigByIds(String[] lotteryIds );

	/**
	 * 删除开奖配置信息
	 *
	 * @param lotteryId 开奖配置ID
	 * @return 结果
	 */
	public int deleteLotteryPrizeconfigById(String lotteryId);
}
