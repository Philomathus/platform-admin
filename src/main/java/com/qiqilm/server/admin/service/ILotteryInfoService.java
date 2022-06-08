package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.*;
import org.apache.ibatis.annotations.Param;

/**
 * 彩票名称Service接口
 *
 * @author 77tv
 * @date 2021-02-23
 */
public interface ILotteryInfoService {

	/**
	 * 查询彩票名称数据
	 *
	 * @param id 彩票名称数据ID
	 * @return 彩票名称数据
	 */
	public LotteryInfo selectLotteryInfoListById(String id );

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


	/**
	 * Update Lottery Status Service InterFace
	 *
	 * @param lotteryInfoSetStatus Update Lottery Status Service InterFace
	 * @return 结果
	 */
     int updateLiveLotterySetStatus(LotteryInfo lotteryInfoSetStatus);


}
