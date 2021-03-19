package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryBet0;

/**
 * 用户投资行为Mapper接口
 *
 * @author 77tv
 * @date 2021-03-03
 */
public interface LotteryBet0Mapper {
	/**
	 * 查询用户投资行为列表
	 *
	 * @param lotteryBet0 用户投资行为
	 * @return 用户投资行为集合
	 */
	public List<LotteryBet0> selectLotteryBet0List(LotteryBet0 lotteryBet0);

	public List<LotteryBet0> selectLotteryBet0SingleList( LotteryBet0 lotteryBet0 );

	public List<LotteryBet0> selectLotteryBet0AbnormalList( LotteryBet0 lotteryBet0 );
}
