package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryPrize;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2022-01-27
 */
public interface LotteryPrizeMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	LotteryPrize selectLotteryPrizeById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param lotteryPrize 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	List<LotteryPrize> selectLotteryPrizeList(LotteryPrize lotteryPrize);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param lotteryPrize 【请填写功能名称】
	 * @return 结果
	 */
	int insertLotteryPrize(LotteryPrize lotteryPrize);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param lotteryPrize 【请填写功能名称】
	 * @return 结果
	 */
	int updateLotteryPrize(LotteryPrize lotteryPrize);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	int deleteLotteryPrizeById(Long id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	int deleteLotteryPrizeByIds(Long[] ids );
}
