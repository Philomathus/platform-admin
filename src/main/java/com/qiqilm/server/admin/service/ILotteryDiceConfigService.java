package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryDiceConfig;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2022-01-27
 */
public interface ILotteryDiceConfigService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	LotteryDiceConfig selectLotteryDiceConfigById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param lotteryDiceConfig 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	List<LotteryDiceConfig> selectLotteryDiceConfigList(LotteryDiceConfig lotteryDiceConfig);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param lotteryDiceConfig 【请填写功能名称】
	 * @return 结果
	 */
	int insertLotteryDiceConfig(LotteryDiceConfig lotteryDiceConfig);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param lotteryDiceConfig 【请填写功能名称】
	 * @return 结果
	 */
	int updateLotteryDiceConfig(LotteryDiceConfig lotteryDiceConfig);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	int deleteLotteryDiceConfigByIds(Long[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	int deleteLotteryDiceConfigById(Long id);
}
