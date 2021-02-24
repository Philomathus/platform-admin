package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryGame;

/**
 * 下注Mapper接口
 *
 * @author 77tv
 * @date 2021-02-23
 */
public interface LotteryGameMapper {
	/**
	 * 查询下注
	 *
	 * @param id 下注ID
	 * @return 下注
	 */
	public LotteryGame selectLotteryGameById(String id);

	/**
	 * 查询下注列表
	 *
	 * @param lotteryGame 下注
	 * @return 下注集合
	 */
	public List<LotteryGame> selectLotteryGameList(LotteryGame lotteryGame);

	/**
	 * 修改下注
	 *
	 * @param lotteryGame 下注
	 * @return 结果
	 */
	public int updateLotteryGame(LotteryGame lotteryGame);

}
