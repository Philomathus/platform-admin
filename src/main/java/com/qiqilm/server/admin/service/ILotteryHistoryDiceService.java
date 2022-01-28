package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryHistoryDice;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2022-01-27
 */
public interface ILotteryHistoryDiceService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	LotteryHistoryDice selectLotteryHistoryDiceById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param lotteryHistoryDice 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	List<LotteryHistoryDice> selectLotteryHistoryDiceList(LotteryHistoryDice lotteryHistoryDice);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param lotteryHistoryDice 【请填写功能名称】
	 * @return 结果
	 */
	int insertLotteryHistoryDice(LotteryHistoryDice lotteryHistoryDice);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param lotteryHistoryDice 【请填写功能名称】
	 * @return 结果
	 */
	int updateLotteryHistoryDice(LotteryHistoryDice lotteryHistoryDice);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	int deleteLotteryHistoryDiceByIds(Long[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	int deleteLotteryHistoryDiceById(Long id);
}
