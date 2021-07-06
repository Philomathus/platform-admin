package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LotteryPrizeconfig;

import java.util.List;

/**
 * 开奖配置Mapper接口
 *
 * @author 77tv
 * @date 2021-03-18
 */
public interface LotteryPrizeconfigMapper {
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
	 * 删除开奖配置
	 *
	 * @param lotteryId 开奖配置ID
	 * @return 结果
	 */
	public int deleteLotteryPrizeconfigById(String lotteryId);

	/**
	 * 批量删除开奖配置
	 *
	 * @param lotteryIds 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLotteryPrizeconfigByIds(String[] lotteryIds );
}
