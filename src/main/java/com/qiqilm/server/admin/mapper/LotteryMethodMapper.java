package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryMethod;

/**
 * 彩票种类Mapper接口
 *
 * @author 77tv
 * @date 2021-02-23
 */
public interface LotteryMethodMapper {

	/**
	 * 查询彩票种类列表
	 *
	 * @param lotteryMethod 彩票种类
	 * @return 彩票种类集合
	 */
	public List<LotteryMethod> selectLotteryMethodList(LotteryMethod lotteryMethod);

}
