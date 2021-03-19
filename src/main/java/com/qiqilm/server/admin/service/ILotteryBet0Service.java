package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LotteryBet0;

/**
 * 用户投资行为Service接口
 *
 * @author 77tv
 * @date 2021-03-03
 */
public interface ILotteryBet0Service {

	/**
	 * 查询用户投资行为列表
	 *
	 * @param lotteryBet0 用户投资行为
	 * @return 用户投资行为集合
	 */
	public List<LotteryBet0> selectLotteryBet0List(LotteryBet0 lotteryBet0);

	/**
	 * 用户投资行为统计
	 *
	 * @param lotteryBet0 用户投资行为
	 * @return 用户投资行为集合
	 */
	AjaxResult getCount(LotteryBet0 lotteryBet0);

}
