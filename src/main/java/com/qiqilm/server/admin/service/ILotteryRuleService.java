package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryRule;

/**
 * 开奖规则说明Service接口
 *
 * @author 77tv
 * @date 2021-02-26
 */
public interface ILotteryRuleService {
	/**
	 * 查询开奖规则说明
	 *
	 * @param id 开奖规则说明ID
	 * @return 开奖规则说明
	 */
	public LotteryRule selectLotteryRuleById(Long id);

	/**
	 * 查询开奖规则说明列表
	 *
	 * @param lotteryRule 开奖规则说明
	 * @return 开奖规则说明集合
	 */
	public List<LotteryRule> selectLotteryRuleList(LotteryRule lotteryRule);

	/**
	 * 新增开奖规则说明
	 *
	 * @param lotteryRule 开奖规则说明
	 * @return 结果
	 */
	public int insertLotteryRule(LotteryRule lotteryRule);

	/**
	 * 修改开奖规则说明
	 *
	 * @param lotteryRule 开奖规则说明
	 * @return 结果
	 */
	public int updateLotteryRule(LotteryRule lotteryRule);

	/**
	 * 批量删除开奖规则说明
	 *
	 * @param ids 需要删除的开奖规则说明ID
	 * @return 结果
	 */
	public int deleteLotteryRuleByIds(Long[] ids );

	/**
	 * 删除开奖规则说明信息
	 *
	 * @param id 开奖规则说明ID
	 * @return 结果
	 */
	public int deleteLotteryRuleById(Long id);
}
