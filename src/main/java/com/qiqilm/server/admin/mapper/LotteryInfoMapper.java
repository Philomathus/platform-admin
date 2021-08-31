package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryInfo;
import com.qiqilm.server.admin.domain.LotteryPrizepool;
import com.qiqilm.server.admin.domain.MemberBcode;

/**
 * 彩票名称Mapper接口
 *
 * @author 77tv
 * @date 2021-02-23
 */
public interface LotteryInfoMapper {
	/**
	 * 查询MemberBcode
	 *
	 * @param id MemberBcodeID
	 * @return MemberBcode
	 */
	public LotteryInfo selectLotteryInfoListById(String id);

	/**
	 * 查询彩票名称列表
	 *
	 * @param lotteryInfo 彩票名称
	 * @return 彩票名称集合
	 */
	public List<LotteryInfo> selectLotteryInfoList(LotteryInfo lotteryInfo);

	/**
	 * 修改彩票名称
	 *
	 * @param lotteryInfo 彩票名称
	 * @return 结果
	 */
	public int updateLotteryInfo(LotteryInfo lotteryInfo);
}
