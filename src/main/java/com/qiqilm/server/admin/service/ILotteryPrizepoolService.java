package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.LotteryPrizepool;

import java.util.List;

/**
 * 奖池配置Service接口
 *
 * @author 77tv
 * @date 2021-03-18
 */
public interface ILotteryPrizepoolService {
	/**
	 * 查询奖池配置
	 *
	 * @param id 奖池配置ID
	 * @return 奖池配置
	 */
	public LotteryPrizepool selectLotteryPrizepoolById(String id);

	/**
	 * 查询奖池配置列表
	 *
	 * @param lotteryPrizepool 奖池配置
	 * @return 奖池配置集合
	 */
	public List<LotteryPrizepool> selectLotteryPrizepoolList(LotteryPrizepool lotteryPrizepool);

	/**
	 * 新增奖池配置
	 *
	 * @param lotteryPrizepool 奖池配置
	 * @return 结果
	 */
	public int insertLotteryPrizepool(LotteryPrizepool lotteryPrizepool);

	/**
	 * 修改奖池配置
	 *
	 * @param lotteryPrizepool 奖池配置
	 * @return 结果
	 */
	public int updateLotteryPrizepool(LotteryPrizepool lotteryPrizepool);

	/**
	 * 批量删除奖池配置
	 *
	 * @param ids 需要删除的奖池配置ID
	 * @return 结果
	 */
	public int deleteLotteryPrizepoolByIds(String[] ids );

	/**
	 * 删除奖池配置信息
	 *
	 * @param id 奖池配置ID
	 * @return 结果
	 */
	public int deleteLotteryPrizepoolById(String id);
}
